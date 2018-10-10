package com.task.demo.controller;

import com.task.demo.model.Author;
import com.task.demo.model.Book;
import com.task.demo.security.CurrentUser;
import com.task.demo.service.AuthorService;
import com.task.demo.service.BookService;
import com.task.demo.util.MailService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
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

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MainController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Value("${book.image}")
    private String bookImageDir;

    @GetMapping("/")
    public String main(@AuthenticationPrincipal CurrentUser currentUser,
                       @ModelAttribute Author author,
                       ModelMap modelMap) {
        verify(currentUser, modelMap);
        show(currentUser, modelMap);
        return "index";
    }

    private boolean verify(@AuthenticationPrincipal CurrentUser currentUser,
                           ModelMap modelMap) {
        if (currentUser != null) {
            modelMap.addAttribute("currentUser", currentUser);
            if (!currentUser.getAuthor().isVerify()) {
                String verifyMsg = "";
                modelMap.addAttribute("verifyMsg", verifyMsg);
            }
        }
        return true;
    }

    private void show(@AuthenticationPrincipal CurrentUser currentUser,
                      ModelMap modelMap) {
        if (currentUser != null) {
            modelMap.addAttribute("currentUser", currentUser.getAuthor());
        }
        List<Author> authors = authorService.findAllAuthors();
        modelMap.addAttribute("authors", authors);
        List<Book> books = bookService.findAllBooks();
        modelMap.addAttribute("books", books);
    }

    @GetMapping("/signIn")
    public String signIn(@ModelAttribute Author author,
                         @AuthenticationPrincipal CurrentUser currentUser,
                         ModelMap modelMap) {
        if (author.getPassword() == null) {
            String errorMsg = "";
            modelMap.addAttribute("errorMsg", errorMsg);
        }
        show(currentUser, modelMap);
        return "index";
    }

    @GetMapping("/user/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/";
    }

    @PostMapping("/user/register")
    public String register(@ModelAttribute Author author,
                           @AuthenticationPrincipal CurrentUser currentUser,
                           ModelMap modelMap) {
        Optional<Author> emailExist = authorService.findByEmail(author.getEmail());
        if (emailExist.isPresent()) {
            String emailExistMsg = "";
            modelMap.addAttribute("emailExistMsg", emailExistMsg);
            show(currentUser, modelMap);
            return "index";
        } else {
            author.setPassword(passwordEncoder.encode(author.getPassword()));
            author.setToken(UUID.randomUUID().toString());
            authorService.save(author);
            logger.info("New user registered_Successfully!");
            String successRegisterMsg = "";
            modelMap.addAttribute("successRegisterMsg", successRegisterMsg);
            show(currentUser, modelMap);
            String url = String.format("http://localhost:8080/verify?token=%s&mail=%s", author.getToken(), author.getEmail());
            String to = author.getEmail();
            String subject = "Hi";
            String text = String.format("%s thank you, you have successfully registered. Please visit by link in order to activate your profile. %s", author.getName(), url);
            mailService.sendSimpleMessage(to, subject, text);
            return "index";
        }
    }

    @GetMapping(value = "/verify")
    public String verify(@RequestParam("mail") String mail,
                         @RequestParam("token") String token) {
        Author byEmail = authorService.findOneByEmail(mail);
        if (byEmail != null && byEmail.getToken() != null && byEmail.getToken().equals(token)) {
            byEmail.setToken(null);
            byEmail.setVerify(true);
            authorService.save(byEmail);
        }
        return "redirect:/user/logout";
    }

    @PostMapping("/book/save")
    public String book(@ModelAttribute Book book,
                       @ModelAttribute Author author,
                       @RequestParam("bookImage") MultipartFile multipartFile,
                       @AuthenticationPrincipal CurrentUser currentUser,
                       ModelMap modelMap) throws IOException {
        File file = new File(bookImageDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String picName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        multipartFile.transferTo(new File(bookImageDir + picName));
        book.setPicUrl(picName);
        if (author.isVerify()) {
            bookService.save(book);
            logger.info("User added a book_Successfully!");
        } else if(!author.isVerify()){
            String bookNotAddedMsg = "";
            modelMap.addAttribute("bookNotAddedMsg", bookNotAddedMsg);
            show(currentUser, modelMap);
            return "index";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/book/getImage")
    public @ResponseBody
    byte[] bookImage(@RequestParam("bookImage") String picUrl) throws IOException {
        InputStream inputStream = new FileInputStream(bookImageDir + picUrl);
        return IOUtils.toByteArray(inputStream);
    }

    @GetMapping("user/deleteBook/{id}")
    public String deleteBook(@PathVariable("id") String id) {
        bookService.deleteBookById(id);
        return "redirect:/";
    }

    @GetMapping("/book{id}")
    public String bookById(@PathVariable("id") ObjectId id,
                           ModelMap modelMap,
                           @AuthenticationPrincipal CurrentUser currentUser) {
        show(currentUser, modelMap);
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
