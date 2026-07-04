package com.ems.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Corporate EMS - Password Reset OTP");

        message.setText(
                "Hello,\n\n"
                        + "Your One-Time Password (OTP) for resetting your password is:\n\n"
                        + otp
                        + "\n\n"
                        + "This OTP is valid for 5 minutes.\n\n"
                        + "If you did not request a password reset, please ignore this email.\n\n"
                        + "Regards,\n"
                        + "Corporate EMS"
        );

        mailSender.send(message);
    }

}
