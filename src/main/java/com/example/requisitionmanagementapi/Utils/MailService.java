package com.example.requisitionmanagementapi.Utils;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MailService {

    private JavaMailSender mailSender;

    public void sendNewAccountEmail(String to, String tempPassword) {
        String subject = "Votre compte a été créé";
        String body = "Bonjour,\n\nVotre compte a été créé. " +
                "Votre mot de passe temporaire est : " + tempPassword + "\n" +
                "Merci de le changer dès votre première connexion.";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Réinitialisation de votre mot de passe";
        String link = "http://localhost:4200/auth/reset-password?token=" + token;
        String body = "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n" + link;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}

