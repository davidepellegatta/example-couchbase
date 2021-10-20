package com.amsdams.couchbase.extractor;

import java.util.List;

public class Init {

  private List<String> dataNodes;

  private String srcUsername;

  private String srcUserPassword;

  private long bufferSize;

  private int threadPoolSize;

  private String srcBucketName;


  public Init(List<String> dataNodes, String srcBucketName, String srcUsername, String srcUserPassword, long bufferSize, int threadPoolSize) {
    this.dataNodes = dataNodes;
    this.srcUsername = srcUsername;
    this.srcUserPassword = srcUserPassword;
    this.bufferSize = bufferSize;
    this.threadPoolSize = threadPoolSize;
    this.srcBucketName = srcBucketName;
  }


  public List<String> getDataNodes() {
    return dataNodes;
  }

  public String getSrcUsername() {
    return srcUsername;
  }

  public String getSrcUserPassword() {
    return srcUserPassword;
  }

  public long getBufferSize() {
    return bufferSize;
  }

  public int getThreadPoolSize() {
    return threadPoolSize;
  }

  public String getSrcBucketName() {
    return srcBucketName;
  }
}
