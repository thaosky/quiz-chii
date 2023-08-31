package com.quizchii.security;

import antlr.Token;
import com.quizchii.common.RoleEnum;
import com.quizchii.entity.RoleEntity;
import com.quizchii.entity.UserEntity;
import com.quizchii.common.BusinessException;
import com.quizchii.entity.VerificationToken;
import com.quizchii.model.ResponseData;
import com.quizchii.model.request.ChangePasswordRequest;
import com.quizchii.model.request.LoginRequest;
import com.quizchii.model.request.RegisterRequest;
import com.quizchii.model.response.JwtResponse;
import com.quizchii.model.response.UserResponse;
import com.quizchii.repository.RoleRepository;
import com.quizchii.repository.UserRepository;
import com.quizchii.repository.VerificationTokenRepository;
import com.quizchii.security.jwt.JwtUtils;
import com.quizchii.security.services.UserDetailsImpl;
import com.quizchii.common.MessageCode;
import com.quizchii.service.SendMailService;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {
    final AuthenticationManager authenticationManager;

    final UserRepository userRepository;

    final RoleRepository roleRepository;

    final PasswordEncoder encoder;

    final JwtUtils jwtUtils;

    final JavaMailSender mailSender;


    final SendMailService sendMailService;

    private final VerificationTokenRepository tokenRepository;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils, JavaMailSender mailSender, SendMailService sendMailService, VerificationTokenRepository tokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.mailSender = mailSender;
        this.sendMailService = sendMailService;
        this.tokenRepository = tokenRepository;
    }

    /**
     * User đăng ký tài khoản, chỉ có thể đăng ký role User
     *
     * @param signUpRequest
     * @return
     */
//    public UserResponse registerUser(RegisterRequest signUpRequest) {
//
//        // Check username, password
//        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.USERNAME_ALREADY_EXIST);
//        }
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.EMAIL_ALREADY_EXIST);
//        }
//
//        UserEntity user = new UserEntity(signUpRequest.getUsername(), signUpRequest.getName(),
//                signUpRequest.getEmail(),
//                encoder.encode(signUpRequest.getPassword()));
//
//        // Set role mặc định là USER
//        Set<RoleEntity> roles = new HashSet<>();
//        RoleEntity userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
//                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.ROLE_NOT_FOUND));
//        roles.add(userRole);
//
//        user.setRoles(roles);
//        UserEntity userEntity = userRepository.save(user);
//
//        // Verify email
//        String token = UUID.randomUUID().toString();
//        createVerificationTokenForUser(user.getId(), token);
//
//        UserResponse response = new UserResponse();
//        response.setUsername(user.getUsername());
//        response.setActive(user.getActive());
//        response.setEmail(user.getEmail());
//        response.setRole(userEntity.getRoles());
//        return response;
//    }
    public UserEntity registerUser(RegisterRequest signUpRequest) {

        // Check username, password
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.USERNAME_ALREADY_EXIST);
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.EMAIL_ALREADY_EXIST);
        }

        UserEntity user = new UserEntity(signUpRequest.getUsername(), signUpRequest.getName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        // Set role mặc định là USER
        Set<RoleEntity> roles = new HashSet<>();
        RoleEntity userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.ROLE_NOT_FOUND));
        roles.add(userRole);

        user.setRoles(roles);
        UserEntity userEntity = userRepository.save(user);

        // Verify email
        String token = UUID.randomUUID().toString();
        createVerificationTokenForUser(user.getId(), token);

        return userEntity;
    }

    public void createVerificationTokenForUser(final Long userId, final String token) {
        final VerificationToken myToken = new VerificationToken(token, userId);
        tokenRepository.save(myToken);
    }

    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
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
            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.USERNAME_ALREADY_EXIST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.EMAIL_ALREADY_EXIST);
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
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.ROLE_NOT_FOUND));
            roles.add(userRole);
        } else {
            // Nếu có role, tạo role như mong muốn
            strRoles.forEach(role -> {
                if ("admin".equals(role)) {
                    RoleEntity adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                            .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.ROLE_NOT_FOUND));
                    roles.add(adminRole);
                } else {
                    RoleEntity userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                            .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.ROLE_NOT_FOUND));
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

        // User active
        Boolean active = userRepository.existsByUsernameAndActive(userName, 1);
        if (!active) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.USER_NOT_ACTIVE);
        }
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
        UserEntity userEntity = userRepository.getByUsername(userName);
        if (!userEntity.isEnabled()) {
            throw new BusinessException(HttpStatus.FORBIDDEN, MessageCode.NOT_VERIFY);
        }
        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userEntity.getName(), userDetails.getEmail(), roles);
    }

    public ResponseData changePassword(Long id, String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String login = authentication.getName();

        List<String> roles = authentication
                .getAuthorities()
                .stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());


        Optional<UserEntity> optional = userRepository.findById(id);
        UserEntity userEntityChangePass = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.USER_NOT_EXIST));
        String passwordEncoder = encoder.encode(password);
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                userEntityChangePass.setPassword(passwordEncoder);
                userRepository.save(userEntityChangePass);
                isAdmin = true;
                break;
            }
        }

        if (!isAdmin) {
            if (login.equals(userEntityChangePass.getUsername())) {
                // đổi pass
                userEntityChangePass.setPassword(passwordEncoder);
                userRepository.save(userEntityChangePass);
            } else {
                throw new BusinessException(HttpStatus.FORBIDDEN, MessageCode.FORBIDDEN);
            }
        }

        ResponseData responseData = new ResponseData<>();
        responseData.setData(MessageCode.CHANGE_PASSWORD_SUCCESS);
        return responseData;
    }

    public ResponseData changePassword(Long id, ChangePasswordRequest request) {
        Optional<UserEntity> optional = userRepository.findById(id);
        UserEntity userEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.USER_NOT_EXIST));

        boolean isMatch = encoder.matches(request.getOldPassword(), userEntity.getPassword());
        if (!isMatch) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.WRONG_PASSWORD);
        } else {
            String passwordEncoder = encoder.encode(request.getNewPassword());
            // đổi pass
            userEntity.setPassword(passwordEncoder);
            userRepository.save(userEntity);
            ResponseData responseData = new ResponseData<>();
            responseData.setData(MessageCode.CHANGE_PASSWORD_SUCCESS);
            return responseData;
        }

    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication
                .getAuthorities()
                .stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());

        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param id user bi tac dong
     * @return
     */
    public boolean havePermission(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        List<String> roles = authentication
                .getAuthorities()
                .stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());

        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                return true;
            }
        }
        Optional<UserEntity> optional = userRepository.findById(id);
        UserEntity userEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.USER_NOT_EXIST));
        return userEntity.getUsername().equals(login);
    }

    public ResponseData userConfirmRegistration(String token) {

        final VerificationToken verificationToken = getVerificationToken(token);

        // không tìm được token
        if (verificationToken == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.TOKEN_NOT_VALID);
        }

        final Long userId = verificationToken.getUserId();
        final Calendar cal = Calendar.getInstance();

        // Hết hạn token
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new BusinessException(HttpStatus.FORBIDDEN, MessageCode.TOKEN_EXPIRY);
        }

        UserEntity user = userRepository.findById(userId).get();
        user.setEnabled(true);
        userRepository.save(user);
        String mess = MessageCode.VERIFICATION_SUCCESS;
        ResponseData response = new ResponseData();
        response.success(mess);
        return response;
    }


    public void serverConfirmRegistration(UserEntity userEntity) {
        final Long userId = userEntity.getId();
        final String token = UUID.randomUUID().toString();
        createVerificationTokenForUser(userId, token);

        final SimpleMailMessage email = sendMailService.constructEmailMessage(userEntity, token);
        mailSender.send(email);
    }

    /**
     * @param userId
     * @return VerificationToken đã được update token mới
     */
    private VerificationToken generateNewToken(Long userId) {
        VerificationToken verifyToken = tokenRepository.findByUserId(userId);
        verifyToken.updateToken(UUID.randomUUID().toString());
        verifyToken = tokenRepository.save(verifyToken);
        return verifyToken;
    }

    public boolean resendRegistrationToken(String email) {
        // Kiểm tra xem email đã đăng ký chưa
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.EMAIL_NOT_EXIST)
        );

        // Generate new token cùng thời gian verify
        VerificationToken newToken = generateNewToken(userEntity.getId());

        // Gửi email
        final SimpleMailMessage content = sendMailService.constructEmailMessage(userEntity, newToken.getToken());
        mailSender.send(content);
        return true;
    }
}
