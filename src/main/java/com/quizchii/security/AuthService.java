package com.quizchii.security;

import com.quizchii.common.RoleEnum;
import com.quizchii.entity.RoleEntity;
import com.quizchii.entity.UserEntity;
import com.quizchii.common.BusinessException;
import com.quizchii.model.ResponseData;
import com.quizchii.model.request.LoginRequest;
import com.quizchii.model.request.RegisterRequest;
import com.quizchii.model.response.JwtResponse;
import com.quizchii.model.response.UserResponse;
import com.quizchii.repository.RoleRepository;
import com.quizchii.repository.UserRepository;
import com.quizchii.security.jwt.JwtUtils;
import com.quizchii.security.services.UserDetailsImpl;
import com.quizchii.common.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    final AuthenticationManager authenticationManager;

    final UserRepository userRepository;

    final RoleRepository roleRepository;

    final PasswordEncoder encoder;

    final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * User đăng ký tài khoản, chỉ có thể đăng ký role User
     *
     * @param signUpRequest
     * @return
     */
    public UserResponse registerUser(RegisterRequest signUpRequest) {

        // Check username, password
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, StatusCode.USERNAME_ALREADY_EXIST);
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, StatusCode.EMAIL_ALREADY_EXIST);
        }

        UserEntity user = new UserEntity(signUpRequest.getUsername(), signUpRequest.getName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        // Set role mặc định là USER
        Set<RoleEntity> roles = new HashSet<>();
        RoleEntity userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, StatusCode.ROLE_NOT_FOUND));
        roles.add(userRole);

        user.setRoles(roles);
        UserEntity userEntity = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setUsername(user.getUsername());
        response.setActive(user.getActive());
        response.setEmail(user.getEmail());
        response.setRole(userEntity.getRoles());
        return response;
    }


    /**
     * Tạo tài khoản bởi Admin, có thể tạo nhiều role khác nhau
     *
     * @param signUpRequest
     * @return
     */
    public UserResponse createUser(RegisterRequest signUpRequest) {
        // Check username, password
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, StatusCode.USERNAME_ALREADY_EXIST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, StatusCode.EMAIL_ALREADY_EXIST);
        }

        // Create new user's account
        UserEntity user = new UserEntity(signUpRequest.getUsername(), signUpRequest.getName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<RoleEntity> roles = new HashSet<>();

        // Nếu không truyền role, mặc định là user
        if (strRoles == null) {
            RoleEntity userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, StatusCode.ROLE_NOT_FOUND));
            roles.add(userRole);
        } else {
            // Nếu có role, tạo role như mong muốn
            strRoles.forEach(role -> {
                if ("admin".equals(role)) {
                    RoleEntity adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                            .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, StatusCode.ROLE_NOT_FOUND));
                    roles.add(adminRole);
                } else {
                    RoleEntity userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                            .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, StatusCode.ROLE_NOT_FOUND));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        UserEntity userEntity = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setUsername(user.getUsername());
        response.setActive(user.getActive());
        response.setEmail(user.getEmail());
        response.setRole(userEntity.getRoles());
        return response;
    }

    public JwtResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userName = userDetails.getUsername();
        Boolean active = userRepository.existsByUsernameAndActive(userName, 1);
        if (!active) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, StatusCode.USER_NOT_ACTIVE);
        }
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    public ResponseData changePassword(Long id, String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String login = authentication.getName();

        List<String> roles = authentication
                .getAuthorities()
                .stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());


        Optional<UserEntity> optional = userRepository.findById(id);
        UserEntity userEntityChangePass = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, StatusCode.USER_NOT_EXIST));
        String passwordEncoder = encoder.encode(password);
        boolean isAdmin = false;
        for (String role: roles) {
            if("ROLE_ADMIN".equals(role)) {
                userEntityChangePass.setPassword(passwordEncoder);
                userRepository.save(userEntityChangePass);
                isAdmin = true;
                break;
            }
        }

        if (!isAdmin) {
            if(login.equals(userEntityChangePass.getUsername())) {
                // đổi pass
                userEntityChangePass.setPassword(passwordEncoder);
                userRepository.save(userEntityChangePass);
            } else {
                throw new BusinessException(HttpStatus.FORBIDDEN, StatusCode.FORBIDDEN);
            }
        }

        ResponseData responseData = new ResponseData<>();
        responseData.setData(StatusCode.CHANGE_PASSWORD_SUCCESS);
        return responseData;
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication
                .getAuthorities()
                .stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());

        for (String role: roles) {
            if("ROLE_ADMIN".equals(role)) {
               return true;
            }
        }
        return false;

    }
}
