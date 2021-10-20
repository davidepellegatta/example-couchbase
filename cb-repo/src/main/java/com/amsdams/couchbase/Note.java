package com.amsdams.couchbase;

import com.couchbase.client.java.json.JsonObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;

import java.util.Map;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Note {

	@Id
	private String id;

	private Map<String, Object> json;
}
