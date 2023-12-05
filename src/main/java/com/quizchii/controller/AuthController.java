package com.quizchii.controller;

import javax.validation.Valid;

import com.quizchii.model.request.ChangePasswordRequest;
import com.quizchii.model.request.LoginRequest;
import com.quizchii.model.request.RegisterRequest;
import com.quizchii.model.response.JwtResponse;
import com.quizchii.security.AuthService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        return ResponseEntity.ok(authService.registerUser(signUpRequest));
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(id, request));
    }
}