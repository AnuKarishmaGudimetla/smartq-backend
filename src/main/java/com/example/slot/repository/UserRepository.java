package com.example.slot.repository;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.slot.model.User;
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}

