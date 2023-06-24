package com.quizchii.model.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class UpdateUserRequest {

    private String name;

    private String email;

    private Integer active;

    private String password;
}
