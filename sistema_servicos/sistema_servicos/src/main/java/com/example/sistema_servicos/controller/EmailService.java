package com.example.sistema_servicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void enviarEmailHtml(String to, String subject, String content) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true permite conteúdo em HTML

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true); // Passando 'true' para indicar que o conteúdo é HTML

        javaMailSender.send(message);
    }
}