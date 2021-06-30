package com.amsdams.couchbase;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends CouchbaseRepository<Note, String> {

}
