package com.sparta.forusmarket.domain.user.dto.response;

import com.sparta.forusmarket.domain.user.entity.Address;
import com.sparta.forusmarket.domain.user.entity.User;

public record UserResponse(Long id, String name, String email, Address address) {
    
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAddress());
    }
}
