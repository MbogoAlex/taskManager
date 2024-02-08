//package com.example.TaskPro.Services;
//
//import com.example.TaskPro.Models.MailStructure;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//
//@Service
//public class MailService {
//    @Autowired
//    private JavaMailSender mailSender;
//    @Value("${spring.mail.username}")
//    private String fromMail;
//
//    public void sendMail(String mail, MailStructure mailStructure, String attachmentPath) {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//
//            helper.setFrom(fromMail);
//            helper.setTo(mail);
//            helper.setSubject(mailStructure.getSubject());
//            helper.setText(mailStructure.getMessage());
//
//            FileSystemResource file = new FileSystemResource(new File(attachmentPath));
//            helper.addAttachment(file.getFilename(), file);
//
//            mailSender.send(mimeMessage);
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}