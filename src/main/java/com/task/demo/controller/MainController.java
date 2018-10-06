package com.task.demo.controller;

import com.task.demo.model.Author;
import com.task.demo.model.Book;
import com.task.demo.security.CurrentUser;
import com.task.demo.service.AuthorService;
import com.task.demo.service.BookService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${book.image}")
    private String bookImageDir;

    @GetMapping("/")
    public String main(@AuthenticationPrincipal CurrentUser currentUser,
                       ModelMap modelMap) {
        if (currentUser != null) {
            modelMap.addAttribute("currentUser", currentUser.getAuthor());
        }
        List<Author> authors = authorService.findAllAuthors();
        modelMap.addAttribute("authors", authors);
        List<Book> books = bookService.findAllBooks();
        modelMap.addAttribute("books", books);
        return "index";
    }

    @GetMapping("/signIn")
    public String signIn() {
        return "redirect:/";
    }

    @PostMapping("/user/register")
    public String register(@ModelAttribute Author author) {
        Optional<Author> emailExist = authorService.findByEmail(author.getEmail());
        if (emailExist.isPresent()) {
            return "redirect:/";
        } else {
            author.setPassword(passwordEncoder.encode(author.getPassword()));
            authorService.save(author);
            logger.info("New user registered_Successfully!");
            return "redirect:/";
        }
    }


    @PostMapping("/book/save")
    public String book(@ModelAttribute Book book,
                       @RequestParam("bookImage") MultipartFile multipartFile) throws IOException {
        File file = new File(bookImageDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String picName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        multipartFile.transferTo(new File(bookImageDir + picName));
        book.setPicUrl(picName);
        bookService.save(book);
        logger.info("User added a book_Successfully!");
        return "redirect:/";
    }

    @GetMapping(value = "/book/getImage")
    public @ResponseBody
    byte[] bookImage(@RequestParam("bookImage") String picUrl) throws IOException {
        InputStream inputStream = new FileInputStream(bookImageDir + picUrl);
        return IOUtils.toByteArray(inputStream);
    }

    @GetMapping("/user/deleteAllBooks")
    public String deleteAllBooks() {
        bookService.deleteAllBooks();
        return "redirect:/";
    }

    @GetMapping("user/deleteBook/{id}")
    public String deleteBook(@PathVariable("id") String id) {
        bookService.deleteBookById(id);
        return "redirect:/";
    }

    @GetMapping("/book{id}")
    public String bookById(@PathVariable("id") String id,
                           ModelMap modelMap,
                           @AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            modelMap.addAttribute("currentUser", currentUser.getAuthor());
        }
        List<Book> allBooks = bookService.findAllBooksById(id);
        modelMap.addAttribute("allBooks", allBooks);
        return "book";
    }

    @PostMapping("/book/update")
    public String updateBook(@ModelAttribute Book book, Book currentBook,
                             @AuthenticationPrincipal CurrentUser currentUser) {

        currentBook.setName(book.getName());
        currentBook.setDescription(book.getDescription());
        currentBook.setAuthor(currentUser.getAuthor());
        currentBook.setPicUrl(book.getPicUrl());
        bookService.save(currentBook);
        return "redirect:/";
    }
}
