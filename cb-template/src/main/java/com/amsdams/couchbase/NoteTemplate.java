package com.amsdams.couchbase;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;

@Service
public class NoteTemplate {

	@Autowired
	CouchbaseTemplate mongoTemplate;

	public Note save(Note note) {
		return mongoTemplate.insertById(Note.class).one(note);
	}

	public Optional<Note> findById(String id) {
		return Optional.of(mongoTemplate.findById(Note.class).one(id));
	}

}
