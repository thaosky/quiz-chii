package com.quizchii.model.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class UpdateUserRequest {

    private String name;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> role;

    private Integer active;
}
