package com.amsdams.couchbase;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

import java.util.UUID;

@SpringBootTest
@Slf4j
class CbTemplateApplicationTests {

	@Autowired
	private CouchbaseTemplate couchbaseTemplate;

	@Test
	void testMapIsTheSame() {

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<note>\n" + "  <to>Tove</to>\n"
				+ "  <from>Jani</from>\n" + "  <heading>Reminder</heading>\n"
				+ "  <body>Don't forget me this weekend!</body>\n" + "</note>";

		String id = UUID.randomUUID().toString();

		JSONObject xmlParser = XML.toJSONObject(xml);

		JsonObject toSave = JsonObject.create().put("originalContent", xmlParser.toMap());


		Collection cc = couchbaseTemplate.getCollection(null);

		cc.upsert(id, toSave);

		JsonObject saved = cc.get(id).contentAsObject();

		log.info(String.format("toSave %s", toSave.toString()));
		log.info(String.format("saved %s", saved.toString()));

		Assertions.assertEquals(toSave, saved);

	}

}
