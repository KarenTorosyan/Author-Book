package com.task.demo.service;

import com.task.demo.model.Book;
import com.task.demo.repository.BookRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public void deleteBookById(String id) {
        bookRepository.deleteById(id);
    }

    public List<Book> findAllBooksById(ObjectId id) {
        return bookRepository.findBookByAuthorId(id);
    }
}
