package com.example.sistema_servicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/email/enviar")
    public String enviarEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String content) {
        // Call the email service to send the email
        emailService.enviarEmail(to, subject, content);
        return "redirect:/servicos"; // Redirect to the services list page
    }
}