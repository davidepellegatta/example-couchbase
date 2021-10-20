package com.amsdams.couchbase.extractor;

import com.couchbase.client.java.Bucket;

import java.util.concurrent.BlockingQueue;

public class QueueConsumer implements Runnable {

  private BlockingQueue<DocumentToProcess> queue;
  private Bucket bucket;

  public QueueConsumer(BlockingQueue<DocumentToProcess> queue, Bucket bucket) {
    this.queue = queue;
    this.bucket = bucket;
  }

  @Override
  public void run() {
    try {

      while(true) {
        DocumentToProcess doc = queue.take();

        bucket.remove(doc.getId());
      }



    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
