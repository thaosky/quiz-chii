package com.quizchii.controller;

import com.quizchii.model.ResponseData;
import com.quizchii.model.request.RegisterRequest;
import com.quizchii.model.request.UpdateUserRequest;
import com.quizchii.security.AuthService;
import com.quizchii.service.impl.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAll(@Param("pageSize") Integer pageSize,
                                    @Param("pageNo") Integer pageNo,
                                    @Param("sortName") String sortName,
                                    @Param("sortDir") String sortDir,
                                    @Param("content") String username,
                                    @Param("tagId") String name) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(userService.getAll(pageSize, pageNo, sortName, sortDir, username, name)));
    }


    // Update thông tin user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUserById(id, request));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(userService.getUserById(id))
        );
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        return ResponseEntity.ok(authService.createUser(signUpRequest));
    }
}
