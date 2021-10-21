package com.amsdams.couchbase.extractor.batch;

import com.couchbase.client.dcp.Client;
import com.couchbase.client.dcp.StreamFrom;
import com.couchbase.client.dcp.StreamTo;
import com.couchbase.client.dcp.config.DcpControl;
import com.couchbase.client.dcp.message.DcpDeletionMessage;
import com.couchbase.client.dcp.message.DcpExpirationMessage;
import com.couchbase.client.dcp.message.DcpMutationMessage;
import com.couchbase.client.dcp.message.DcpSnapshotMarkerRequest;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.DeserializationFeature;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


public class RemoveExpiredDocuments {

  static protected final Logger LOGGER = Logger.getLogger(RemoveExpiredDocuments.class);
  private static ObjectMapper MAPPER = new ObjectMapper();
  private static BlockingQueue<DocumentToProcess> queue;
  private static int missCount = 0;
  private static int diffCount = 0;
  private static AtomicLong missingDocs = new AtomicLong(0);
  private Bucket destBucket = null;
  private List<String> dataNodes;
  private String srcUsername;
  private String srcUserPassword;
  private long bufferSize;
  private int threadPoolSize;
  private String srcBucketName;


  public RemoveExpiredDocuments(Init input) {

    LocalDateTime start = LocalDateTime.now();

    this.srcBucketName = input.getSrcBucketName();
    this.srcUsername = input.getSrcUsername();
    this.srcUserPassword = input.getSrcUserPassword();
    this.bufferSize = input.getBufferSize();
    this.threadPoolSize = input.getThreadPoolSize();
    this.dataNodes = input.getDataNodes();

    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    queue = new LinkedBlockingQueue<>();

    LOGGER.debug("this.dataNodes = " + this.dataNodes);
    LOGGER.debug("this.srcBucketName = " + this.srcBucketName);
    LOGGER.debug("this.srcUsername = " + this.srcUsername);
    LOGGER.debug("this.srcUserPassword = " + this.srcUserPassword);
    LOGGER.debug("this.bufferSize = " + this.bufferSize);
    LOGGER.debug("this.threadPoolSize = " + this.threadPoolSize);

    // Connect to localhost and use the travel-sample bucket
    final Client client = Client.builder()
        .seedNodes(this.dataNodes)
        .bucket(this.srcBucketName)
        .credentials(this.srcUsername, this.srcUserPassword)
        .controlParam(DcpControl.Names.CONNECTION_BUFFER_SIZE, this.bufferSize) // set the buffer to 1GB
        .bufferAckWatermark(75) // after 75% are reached of the 1GB, acknowledge against the server
        .build();

    Cluster cluster = CouchbaseCluster.create(this.dataNodes);
    cluster.authenticate(this.srcUsername, this.srcUserPassword);

    destBucket = cluster.openBucket(this.srcBucketName);


    // Don't do anything with control events in this example
    client.controlEventHandler((flowController, event) -> {
      if (DcpSnapshotMarkerRequest.is(event)) {
        flowController.ack(event);
      }
      event.release();
    });

    // Acknowledge bytes to let it move on...
    final AtomicLong mutationEventProcessed = new AtomicLong(0);
    final AtomicLong attachmentDocs = new AtomicLong(0);
    final AtomicLong otherDocsToIgnore = new AtomicLong(0);


    client.dataEventHandler((flowController, event) -> {
      if (DcpMutationMessage.is(event)) {
        String keyID = DcpMutationMessage.keyString(event);

        try {

          String content = DcpMutationMessage.content(event).toString(StandardCharsets.UTF_8);

          if (content != null && content.contains("read")) {
            queue.put(new DocumentToProcess(keyID));
          }

        } catch (Exception e) {
          LOGGER.error(Thread.currentThread().getName() + " => JSON parsing exception with the DCP content  " + e.getMessage() + " for Doc ID :: " + keyID);
        }
        mutationEventProcessed.incrementAndGet();

      } else  if (DcpExpirationMessage.is(event)) {
        String keyID = DcpExpirationMessage.keyString(event);
        LOGGER.info("DcpExpirationMessage event IGNORED :: " + keyID);
      } else  if (DcpDeletionMessage.is(event)) {
        String keyID = DcpDeletionMessage.keyString(event);
        LOGGER.info("DcpDeletionMessage event IGNORED :: " + keyID);
      }
      // this method will acknowledge the bytes for mutation, deletion and expiration
      flowController.ack(event);
      event.release();
    });

    LOGGER.info("Current queue size :: " + queue.size());

    // Connect the sockets
    client.connect().await();

    // Initialize the state (start now, never stop)
    client.initializeState(StreamFrom.BEGINNING, StreamTo.NOW).await();

    // Start streaming on all partitions
    client.startStreaming().await();

    // Run the queue poll in a new thread
    ExecutorService executorService = Executors.newFixedThreadPool(this.threadPoolSize);

    for (int i = 1; i <= this.threadPoolSize; i++) {
      executorService.submit(new QueueConsumer(queue, destBucket));
    }

    // Sleep and wait until the DCP stream has caught up with the time where we said "now".
    while (true) {
      showResults(mutationEventProcessed, attachmentDocs, otherDocsToIgnore);
      if (client.sessionState().isAtEnd() && queue.isEmpty()) {
        LOGGER.info("DCP stream is finished and queue is empty.");
        break;
      }
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        LOGGER.error(e);
      }
    }

    try {
      executorService.shutdown();
      LOGGER.info("executorService await termination :: 30s...");
      executorService.awaitTermination(30, TimeUnit.SECONDS);
      LOGGER.info("executorService await termination :: 30s... done!");
    } catch (InterruptedException e) {
      LOGGER.error(e);
    }

    LocalDateTime end = LocalDateTime.now();

    Duration duration = Duration.between(start, end);

    long durationInMinutes = duration.toMinutes();
    LOGGER.info("Total duration :: " + durationInMinutes + " minutes");

  }

  private void showResults(final AtomicLong mutationEventProcessed, final AtomicLong attachmentDocs, final AtomicLong otherDocsToIgnore) {
    LOGGER.info("|-------------------------------------");
    LOGGER.info("Docs processed so far            :: " + mutationEventProcessed.get());
    LOGGER.info("Current queue size -- while true :: " + queue.size());
    LOGGER.info("|-------------------------------------");
  }
}



