package com.simple.core.service;

import com.simple.core.entity.User;
import com.simple.core.error.TokenExpiredException;
import com.simple.core.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CoreService {

    @Autowired
    UserRepository userRepository;

    public Boolean validateUserTokenAndCardId(String userToken, String cardId) {
        return userRepository.validateUserTokenAndCardId(userToken, cardId);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(TokenExpiredException::new);
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }
}
