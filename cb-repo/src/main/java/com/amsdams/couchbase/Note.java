package com.amsdams.couchbase;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.index.CompositeQueryIndex;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.couchbase.client.java.json.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;


@Document
@Data
@JsonInclude(content = Include.ALWAYS)
@CompositeQueryIndex(fields = {"id", "json"})
@With
@AllArgsConstructor
@NoArgsConstructor
public class Note {
	

	
	@Id @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
	//@Field
	//@QueryIndexed
    private String id;
	@Field
	@QueryIndexed
	@JsonInclude(content = Include.ALWAYS)
	@AccessType(AccessType.Type.PROPERTY)
	private JsonObject json;
	/*
	@CreatedBy private String creator;

	@LastModifiedBy private String lastModifiedBy;

	@LastModifiedDate private long lastModification;

	@CreatedDate private long creationDate;

	@Version private long version;
	*/
	
}
