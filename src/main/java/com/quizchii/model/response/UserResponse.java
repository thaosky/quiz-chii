package com.quizchii.model.response;

import com.quizchii.entity.RoleEntity;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {

    private String username;

    private String name;

    private String email;

    private Integer active;

    private Set<RoleEntity> role;
}
