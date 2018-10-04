package com.task.demo.service;
import com.task.demo.model.Author;
import com.task.demo.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public Author save(Author author){
        return authorRepository.save(author);
    }

    public List<Author> findAllAuthors(){
        return authorRepository.findAll();
    }

}
