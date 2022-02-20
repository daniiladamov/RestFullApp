package com.adamov.shortlink.services;

import com.adamov.shortlink.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServices {
    private final UserRepository userRepository;

    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean findById(String userId) {
        if (userRepository.findById(Long.valueOf(userId)).orElse(null)!=null)
            return true;
        else return false;
    }
}
