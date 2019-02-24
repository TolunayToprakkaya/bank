package com.example.bank.service;

import com.example.bank.model.User;

import java.util.List;

public interface UserService {

    User save(User user);
    List<User> findAll();
    User getUserByEmail(String email);

}
