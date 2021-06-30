package com.amsdams.couchbase;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Document
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class Note {

	@Id
	// @Field
	// @QueryIndexed
	private String id;

	private Map<String, Object> json;
	/*
	 * @CreatedBy private String creator;
	 * 
	 * @LastModifiedBy private String lastModifiedBy;
	 * 
	 * @LastModifiedDate private long lastModification;
	 * 
	 * @CreatedDate private long creationDate;
	 * 
	 * @Version private long version;
	 */

}
