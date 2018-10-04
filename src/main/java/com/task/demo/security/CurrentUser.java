package com.task.demo.security;

import com.task.demo.model.Author;
import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;

@Getter
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    public Author author;

    public CurrentUser(Author author) {
        super(author.getEmail(), author.getPassword(), AuthorityUtils.NO_AUTHORITIES);
        this.author = author;
    }
}
