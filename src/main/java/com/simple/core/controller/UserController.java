package com.simple.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.simple.core.entity.User;
import com.simple.core.service.CoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("core")
public class UserController {

    @Autowired
    private CoreService service;

    @GetMapping("/validate/{userToken}/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean validateUserTokenAndCardId(@PathVariable String userToken, @PathVariable String cardId) {
        return service.validateUserTokenAndCardId(userToken, cardId);
    }

    @PostMapping("/reserve/{eventId}/{seatId}/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public Long reserveSeatAndPay(@PathVariable Long eventId, @PathVariable String seatId, @PathVariable String cardId) throws JsonProcessingException {
        return service.reserveSeatAndPay(eventId, seatId, cardId);
    }

    @GetMapping("/find/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User findById(@PathVariable Long userId) {
        return service.findUserById(userId);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<User> listAllUsers() {
        return service.listAllUsers();
    }
}
