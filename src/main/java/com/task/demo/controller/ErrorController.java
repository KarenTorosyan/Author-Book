package com.task.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("/error")
    public String handleError() {
        return "404";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

