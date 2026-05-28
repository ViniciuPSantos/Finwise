package com.finwise.finwise.auth.dto;

import com.finwise.finwise.auth.User;

public record UserResponse(Long id, String name, String email){
    public static UserResponse from(User user){
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
