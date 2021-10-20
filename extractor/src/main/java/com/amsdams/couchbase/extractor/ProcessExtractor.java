package com.amsdams.couchbase.extractor;


import java.util.List;

public class ProcessExtractor {

  public static void main(String[] args) {


    //List<String> dataNodes, String srcBucketName, String srcUsername, String srcUserPassword, long bufferSize, int threadPoolSize
    Init configuration = new Init(
        List.of("localhost"),
        "test",
        "Administrator",
        "couchbase",
        1000,
        4
    );

    RemoveExpiredDocuments expiredDocuments = new RemoveExpiredDocuments(configuration);

  }

}
