package com.example.project.mapper;

import com.example.project.dto.UserDto;
import com.example.project.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto mapToDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    public User mapToEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return User.builder()
                .id(userDto.getId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .build();
    }

}
