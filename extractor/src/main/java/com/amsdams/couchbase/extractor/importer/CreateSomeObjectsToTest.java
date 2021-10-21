package com.amsdams.couchbase.extractor.importer;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class CreateSomeObjectsToTest {

  public static void main(String[] args) throws InterruptedException {

    Cluster cluster = CouchbaseCluster.create("localhost");
    cluster.authenticate("Administrator", "couchbase");

    AsyncBucket bucket = cluster.openBucket("test").async();

    int docsToCreate = 1000000;
    List<JsonDocument> documents = new ArrayList<>();

    for (int i = 0; i < docsToCreate; i++) {
      JsonObject content = JsonObject.create()
          .put("counter", i)
          .put("type", "myType")
          .put("status", ((i % 2) == 0) ? "read" : "new");

      documents.add(JsonDocument.create("doc-"+i, content));
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
}
