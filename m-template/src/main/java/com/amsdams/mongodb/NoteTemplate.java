package com.amsdams.mongodb;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class NoteTemplate {

	@Autowired
	MongoTemplate mongoTemplate;

	public Note save(Note note) {
		// TODO Auto-generated method stub
		return mongoTemplate.save(note);
	}

	public Optional<Note> findById(String id) {
		// TODO Auto-generated method stub
		return Optional.of(mongoTemplate.findById(id, Note.class));
	}

}
