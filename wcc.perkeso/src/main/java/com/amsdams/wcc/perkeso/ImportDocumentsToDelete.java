package com.amsdams.wcc.perkeso;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ImportDocumentsToDelete {

  public static void main(String[] args) throws InterruptedException {

    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    Cluster cluster = CouchbaseCluster.create("3.67.41.91");
    cluster.authenticate("Administrator", "Administrator");

    AsyncBucket bucket = cluster.openBucket("poc").async();

    int docsToCreate = 1000000;
    List<JsonDocument> documents = new ArrayList<>();


    SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    long aDay = TimeUnit.DAYS.toMillis(1);

    Date nowDate = new Date();

    long now = nowDate.getTime();

    Date tenDaysAgo = new Date(now - aDay * 10);

    for (int i = 0; i < docsToCreate; i++) {

      JsonObject content = JsonObject.create()
          .put("counter", i)
          .put("_class", "com.wcc.swipe.job.document.NotificationDocument")
          .put("type","JOB_MATCH_NOTIFICATION")
          .put("creationDate", String.format("%s[UTC]", ISO8601DATEFORMAT.format(between(tenDaysAgo, nowDate))));

      documents.add(JsonDocument.create(UUID.randomUUID().toString(), content));
    }

    // Insert them in one batch, waiting until the last one is done.
    Observable
        .from(documents)
        .buffer(3000)
        .observeOn(Schedulers.computation())
        .doOnNext( (list) -> Observable.from(list).flatMap(bucket::upsert).last()
            .toBlocking()
            .single()
        )
        .subscribe();

    Thread.sleep(500000);
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
