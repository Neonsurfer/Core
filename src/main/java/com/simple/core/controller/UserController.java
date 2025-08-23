package com.simple.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/findById/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void something() {
    }
}
