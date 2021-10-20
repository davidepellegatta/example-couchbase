package com.amsdams.couchbase;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.couchbase.client.java.json.JsonObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.couchbase.core.mapping.CouchbaseDocument;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
public class NoteRepositoryTest {

	@Autowired
	NoteRepository noteRepository;



	@Test
	void create() throws JSONException {

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<note>\n" + "  <to>Tove</to>\n"
				+ "  <from>Jani</from>\n" + "  <heading>Reminder</heading>\n"
				+ "  <body>Don't forget me this weekend!</body>\n" + "</note>";

		String id = UUID.randomUUID().toString();

		JSONObject parsedXml = XML.toJSONObject(xml);

		Note note = new Note(id, parsedXml.toMap());

		Note savedTemp = noteRepository.save(note);

		Optional<Note> optionalNote = noteRepository.findById(id);

		log.info("expected {}", note);

		Note noteFound = optionalNote.get();

		log.info("actual {}", (Map<String,Object>)noteFound.getJson());

		Assertions.assertEquals(note.getId(), noteFound.getId());
		Assertions.assertTrue(parsedXml.equals(noteFound.getJson()));

	}

}
