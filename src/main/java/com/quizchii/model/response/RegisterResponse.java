package com.quizchii.model.response;

import lombok.Data;

import java.util.Set;

@Data
public class RegisterResponse {

    private String username;

    private String email;

    private Set<String> role;
}
