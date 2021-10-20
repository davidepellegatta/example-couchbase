package com.amsdams.couchbase;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;

@Configuration
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {
	// clusterName
	@Override
	public String getConnectionString() {
		// TODO Auto-generated method stub
		return "couchbase://127.0.0.1";
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return "Administrator";
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return "couchbase";
	}

	@Override
	public String getBucketName() {
		// TODO Auto-generated method stub
		return "test";
	}

	@Override
	protected boolean autoIndexCreation() {
		// TODO Auto-generated method stub
		return true;
	}

}
