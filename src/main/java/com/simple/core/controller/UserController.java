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

    /**
     * Validates that the user token is connected to give card
     *
     * @param userToken user token to be validated
     * @param cardId    card id subject to validation
     * @return result of validation
     */
    @GetMapping("/validate/{userToken}/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean validateUserTokenAndCardId(@PathVariable String userToken, @PathVariable String cardId) {
        return service.validateUserTokenAndCardId(userToken, cardId);
    }

    /**
     * Tries to reserve a seat for given event.
     * Validates card has enough balance to cover reservation
     *
     * @param eventId id of event subject to reservation
     * @param seatId  id of seat to be reserved
     * @param cardId  user's card id
     * @return if successful, reserves reservation id. Otherwise, returns exception
     * @throws JsonProcessingException if one of the requests returns erroneous response
     */
    @PostMapping("/reserve/{eventId}/{seatId}/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public Long reserveSeatAndPay(@PathVariable Long eventId, @PathVariable String seatId, @PathVariable String cardId) throws JsonProcessingException {
        return service.reserveSeatAndPay(eventId, seatId, cardId);
    }

    /**
     * Queries single user by id
     *
     * @param userId id of requested user
     * @return User if found, exception otherwise
     */
    @GetMapping("/find/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User findById(@PathVariable Long userId) {
        return service.findUserById(userId);
    }

    /**
     * Lists all users
     *
     * @return List of all users in the database
     */
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<User> listAllUsers() {
        return service.listAllUsers();
    }
}
