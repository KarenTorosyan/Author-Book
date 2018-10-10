package com.task.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailService {

    @Autowired
    public JavaMailSender mailSender;

    public void sendSimpleMessage(String to,String subject,String text){
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(to);
        smm.setSubject(subject);
        smm.setText(text);
        mailSender.send(smm);
    }
}
