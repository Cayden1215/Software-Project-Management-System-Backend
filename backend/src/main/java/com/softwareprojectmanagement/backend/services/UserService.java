package com.softwareprojectmanagement.backend.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.softwareprojectmanagement.backend.dto.UserDto;
import com.softwareprojectmanagement.backend.entities.User;

@Service
public interface UserService {

    public User createUser(UserDto userDto);

    public ResponseEntity<String> deleteUser(Long id);
    
} 
    

    

