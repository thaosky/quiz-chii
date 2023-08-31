package com.quizchii.controller;

import com.quizchii.entity.UserEntity;
import com.quizchii.entity.VerificationToken;
import com.quizchii.model.request.ChangePasswordRequest;
import com.quizchii.model.request.LoginRequest;
import com.quizchii.model.request.RegisterRequest;
import com.quizchii.model.response.JwtResponse;
import com.quizchii.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest, final HttpServletRequest request) {
        UserEntity registered = authService.registerUser(signUpRequest);
        authService.serverConfirmRegistration(registered);
        return ResponseEntity.ok(registered);
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(id, request));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") final String token) {
        return ResponseEntity.ok(authService.userConfirmRegistration(token));
    }

    @PostMapping("/resend-verify-token")
    public ResponseEntity<?> resendRegistrationToken(@RequestParam("token") final String existingToken) {
        return ResponseEntity.ok(authService.resendRegistrationToken(existingToken));
    }
}