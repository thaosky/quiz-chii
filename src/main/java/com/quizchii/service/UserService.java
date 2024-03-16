package com.quizchii.service;

import com.quizchii.common.BusinessException;
import com.quizchii.common.MessageCode;
import com.quizchii.common.Util;
import com.quizchii.entity.UserEntity;
import com.quizchii.model.ListResponse;
import com.quizchii.model.request.UpdateUserRequest;
import com.quizchii.model.response.UserResponse;
import com.quizchii.repository.UserRepository;
import com.quizchii.security.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, AuthService authService, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.encoder = encoder;
    }

    public ListResponse getAll(Integer pageSize, Integer pageNo, String sortName, String sortDir, String username, String name, String email) {

        if ("".equals(username)) {
            username = null;
        }
        if ("".equals(name)) {
            name = null;
        }
        if ("".equals(email)) {
            email = null;
        }
        Pageable pageable = Util.createPageable(pageSize, pageNo, sortName, sortDir);
        Page<UserEntity> page = userRepository.listUser(username, name, email, pageable);

        ListResponse<UserResponse> response = new ListResponse();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (UserEntity userEntity : page.toList()) {
            UserResponse user = new UserResponse();
            BeanUtils.copyProperties(userEntity, user);
            user.setRole(userEntity.getRoles());
            userResponseList.add(user);
        }
        response.setItems(userResponseList);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPage((int) page.getTotalPages());

        return response;
    }

    public UpdateUserRequest updateUserById(Long id, UpdateUserRequest request) {
        if (!authService.havePermission(id)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.FORBIDDEN);
        }
        Optional<UserEntity> optional = userRepository.findById(id);
        UserEntity userEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.USER_NOT_EXIST));
        if (userRepository.existsByEmail(request.getEmail()) && !request.getEmail().equals(userEntity.getEmail())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.EMAIL_ALREADY_EXIST);
        }

        userEntity.setName(request.getName());
        userEntity.setActive(request.getActive());
        if (request.getPassword() != null && !"".equals(request.getPassword())) {
            String newPass = encoder.encode(request.getPassword());
            userEntity.setPassword(newPass);

        }
        userRepository.save(userEntity);
        return request;
    }

    public UserResponse getUserById(Long id) {
        Optional<UserEntity> optional = userRepository.findById(id);

        UserEntity userEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.USER_NOT_EXIST));
        if (!authService.havePermission(id)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.FORBIDDEN);
        }
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(userEntity, response);
        response.setRole(userEntity.getRoles());
        return response;
    }

    public void resetUserLazy() {
        // Khong login pham vi 1 ngay tu thoi diem check
        Timestamp timestamp = Util.addTime(new Timestamp(System.currentTimeMillis()), -24 * 60);
        List<UserEntity> userEntityList = userRepository.findAllByLastActiveBeforeAndActiveAndCurrDaysStreakNot(timestamp, 1, 0);

        for (UserEntity user : userEntityList) {
            user.setCurrDaysStreak(0);
        }

        userRepository.saveAll(userEntityList);
    }
}
