package com.quizchii.service;

import com.quizchii.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class SendMailService {

    @Value("${spring.mail.username}")
    private String mailFrom;


    public SimpleMailMessage constructEmailMessage(final UserEntity user, final String token) {
        final String toEmail = user.getEmail();
        final String subject = "Xác nhận tài khoản QuizChii";
        final String message = "Chào mừng đến với QuizChii \nMã xác nhận: " + token;

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(mailFrom);
        return email;
    }

}
