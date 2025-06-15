package com.zinemasterapp.zinemasterapp.repository;

import com.zinemasterapp.zinemasterapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {//a spingboot JPA interface so i can use all kinds of functions
    Optional<User> findByUsername(String username);//SELECT * FROM users WHERE username = ?, in the background
}

