package com.task.demo.repository;

import com.task.demo.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findBookByAuthorId(String id);
}
