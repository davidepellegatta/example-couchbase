package com.amsdams.couchbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.core.mapping.CouchbaseDocument;
import org.springframework.data.couchbase.core.mapping.CouchbasePersistentEntity;
import org.springframework.data.couchbase.core.mapping.CouchbasePersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.util.TypeInformation;

@SpringBootApplication
public class CbTemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(CbTemplateApplication.class, args);
	}

}
