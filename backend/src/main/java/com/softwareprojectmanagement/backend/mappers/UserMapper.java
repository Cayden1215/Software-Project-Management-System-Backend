package com.softwareprojectmanagement.backend.mappers;

import com.softwareprojectmanagement.backend.entities.Role;

import org.springframework.stereotype.Component;

import com.softwareprojectmanagement.backend.dto.UserDto;
import com.softwareprojectmanagement.backend.entities.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class UserMapper {

    private User user;
    private UserDto userDto;

    public User mapToUser(UserDto userDto) {
        user.setUserID(userDto.getUserID());
        user.setName(userDto.getName());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());

        Role role = Role.valueOf(userDto.getRole());
        user.setRole(role);
        return user;
    }

    public UserDto mapToUserDto(User user) {
        userDto.setUserID(user.getUserID());
        userDto.setName(user.getName());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole().name());
        return userDto;
    }


}
