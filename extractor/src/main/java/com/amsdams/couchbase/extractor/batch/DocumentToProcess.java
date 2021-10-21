package com.amsdams.couchbase.extractor.batch;

public class DocumentToProcess {

  private String id;

  public DocumentToProcess(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
