package com.app.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> signIn(String email, String password){
        return userRepository.findByEmail(email).filter(user -> user.getPassword().equals(password));
    }
}
