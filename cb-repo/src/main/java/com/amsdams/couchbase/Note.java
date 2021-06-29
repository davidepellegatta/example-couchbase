package com.amsdams.couchbase;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.index.CompositeQueryIndex;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.CouchbaseDocument;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.Getter;


@Document
@Data
@JsonInclude(content = Include.ALWAYS)
@CompositeQueryIndex(fields = {"id", "json"})
public class Note {
	

	@Id
	@Field
	@QueryIndexed
    private String id;
	@Field
	@QueryIndexed
	@JsonInclude(content = Include.ALWAYS)
	private CouchbaseDocument json;
	/*
	@CreatedBy private String creator;

	@LastModifiedBy private String lastModifiedBy;

	@LastModifiedDate private long lastModification;

	@CreatedDate private long creationDate;

	@Version private long version;
	*/
	
}
