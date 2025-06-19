package com.zinemasterapp.zinemasterapp.security;


import com.zinemasterapp.zinemasterapp.model.User;
import com.zinemasterapp.zinemasterapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.*;

import org.springframework.stereotype.Service;


//@Service//moze Springboot koga saka da go koristi
public class CustomUserAccess implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not in the DB."));

        if (user.getAccess() == 0) {
            throw new DisabledException("User is inactive.");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getUserType())
                .build();//ova build ni dava da moze da go stavam kako user

    }
}