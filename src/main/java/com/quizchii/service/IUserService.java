package com.quizchii.service;

import com.quizchii.model.ListResponse;
import com.quizchii.model.request.UpdateUserRequest;
import com.quizchii.model.response.UserResponse;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    ListResponse getAll(Integer pageSize, Integer pageNo, String sortName, String sortDir, String username, String name);

    UpdateUserRequest updateUserById(Long id, UpdateUserRequest request);

    UserResponse getUserById(Long id);
}
