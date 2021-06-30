package com.amsdams.couchbase;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.index.CompositeQueryIndex;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Document
@Data
@CompositeQueryIndex(fields = { "id", "json" })
@With
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@JsonInclude(content = Include.ALWAYS)
public class Note {

	@Id
	@GeneratedValue(strategy = GenerationStrategy.UNIQUE)
	// @Field
	// @QueryIndexed
	private String id;
	@Field
	@QueryIndexed

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

	public Map<String, Object> getJson() {
		Map<String, Object> map = json;
		log.info("map {}", map.toString());

		JSONObject obj = new JSONObject(this.json);
		log.info("json {}", obj.toString());
		ObjectMapper oMapper = new ObjectMapper();
		oMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Map<String, Object> mapped = oMapper.convertValue(json, new TypeReference<Map<String, Object>>() {
		});
		log.info("mapped {}", mapped.toString());

		return json;
	}

}
