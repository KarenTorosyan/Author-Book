package com.task.demo.service;

import com.task.demo.model.Book;
import com.task.demo.repository.BookRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book save(Book book){
        return bookRepository.save(book);
    }

    public List<Book> findAllBooks(){
        return bookRepository.findAll();
    }

    public void deleteAllBooks(){
        bookRepository.deleteAll();
    }

    public void deleteBookById(String id){
        bookRepository.deleteById(id);
    }

    public List<Book> findAllBooksById(String id){
        return bookRepository.findBookByAuthorId(id);
    }
}
