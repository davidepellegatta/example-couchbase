package com.amsdams.importer;

import com.couchbase.client.core.BackpressureException;
import com.couchbase.client.core.time.Delay;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.util.retry.RetryBuilder;
import org.apache.commons.cli.*;
import rx.Observable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ImportDocumentsToDelete {

  public static void main(String[] args) throws InterruptedException {


    CommandLine commandLine;
    Option option_h = Option.builder("h").argName("host").hasArg().desc("couchbase ip").build();
    Option option_u = Option.builder("u").argName("username").hasArg().desc("couchbase username").build();
    Option option_p = Option.builder("p").argName("password").hasArg().desc("couchbase password").build();
    Option option_b = Option.builder("b").argName("bucket").hasArg().desc("couchbase bucket").build();
    Option option_docs = Option.builder("n").argName("num-of-docs").hasArg().desc("docs to create").build();
    Options options = new Options();
    CommandLineParser parser = new DefaultParser();

    options.addOption(option_h);
    options.addOption(option_u);
    options.addOption(option_p);
    options.addOption(option_docs);
    options.addOption(option_b);

    String header = "               [<arg1> [<arg2> [<arg3> ...\n       Options, flags and arguments may be in any order";
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("CLIsample", header, options, null, true);

    String username = "Administrator";
    String password = "couchbase";
    String ip = "127.0.0.1";
    String bucketName = "poc";
    int docs = 1_000_000;

    try
    {
      commandLine = parser.parse(options, args);

      if (commandLine.hasOption("h"))
      {
        System.out.println(String.format("host ip: %s", commandLine.getOptionValue("h")));
        ip = commandLine.getOptionValue("h");
      }

      if (commandLine.hasOption("u"))
      {
        System.out.println(String.format("couchbase username: %s", commandLine.getOptionValue("u")));
        username = commandLine.getOptionValue("u");
      }

      if (commandLine.hasOption("p"))
      {
        System.out.println(String.format("couchbase password: %s", commandLine.getOptionValue("p")));
        password = commandLine.getOptionValue("p");
      }

      if (commandLine.hasOption("n"))
      {
        System.out.println(String.format("docs to save: %s", commandLine.getOptionValue("n")));
        docs = Integer.parseInt(commandLine.getOptionValue("n"));
      }
      if (commandLine.hasOption("b"))
      {
        System.out.println(String.format("couchbase bucket: %s", commandLine.getOptionValue("b")));
        bucketName = commandLine.getOptionValue("b");
      }

    }
    catch (ParseException exception)
    {
      System.out.print("Parse error: ");
      System.out.println(exception.getMessage());
    }

    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    Cluster cluster = CouchbaseCluster.create(ip);
    cluster.authenticate(username, password);

    AsyncBucket bucket = cluster.openBucket(bucketName).async();


    SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    long aDay = TimeUnit.DAYS.toMillis(1);
    Date nowDate = new Date();
    long now = nowDate.getTime();

    Date twoYearsAgo = new Date(now - aDay * 730);

    // Insert them in one batch, waiting until the last one is done.
    Observable
        .range(1, docs)
        .map( count -> {
          JsonObject content = JsonObject.create()
              .put("counter", count)
              .put("_class", "test_class")
              .put("type","notification")
              .put("creationDate", String.format("%s[UTC]", ISO8601_DATE_FORMAT.format(between(twoYearsAgo, nowDate))));
          return JsonDocument.create(UUID.randomUUID().toString(), content);
        })
        .flatMap(doc -> bucket.insert(doc)
            .retryWhen(RetryBuilder
                .anyOf(BackpressureException.class)
                .delay(Delay.exponential(TimeUnit.MILLISECONDS, 100))
                .max(10)
                .build()))
        .last()
        .toBlocking()
        .single();
  }

  public static Date between(Date startInclusive, Date endExclusive) {
    long startMillis = startInclusive.getTime();
    long endMillis = endExclusive.getTime();
    long randomMillisSinceEpoch = ThreadLocalRandom
        .current()
        .nextLong(startMillis, endMillis);

    return new Date(randomMillisSinceEpoch);
  }
}
