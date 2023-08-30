package com.quizchii.sendMail;


import java.util.UUID;

import com.quizchii.entity.UserEntity;
import com.quizchii.security.AuthService;
import com.quizchii.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private AuthService authService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;


    @Override
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final OnRegistrationCompleteEvent event) {
        final UserEntity userEntity = event.getUser();
        final Long userId = userEntity.getId();
        final String token = UUID.randomUUID().toString();
        authService.createVerificationTokenForUser(userId, token);

        final SimpleMailMessage email = constructEmailMessage(event, userEntity, token);
        mailSender.send(email);
    }

    private SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final UserEntity user, final String token) {
        final String toEmail = user.getEmail();
        final String subject = "Xác nhận tài khoản QuizChii";
        final String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
        final String message = messages.getMessage("message.regSuccLink", null,
                "Chào mừng đến với QuizChii. Để xác nhận tài khoản, bạn nhấn vào link này nhé.", event.getLocale());

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }


}
