package com.sasi.quickbooks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @SuppressWarnings("unused")
    public void sendSimpleMailMessage(String from, String to, String subject, String text) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(text);

            mailSender.send(mailMessage);
        } catch (MailException e) {
            log.error(e.getMessage());
        }
    }

    public boolean sendMailWithAttachment(String to, String subject, String body, File attachment1, File attachment2) {
        MimeMessagePreparator preparator = (MimeMessage mimeMessage) -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setFrom("xyz@gmail.com");
            helper.setText(body);
            mimeMessage.setSubject(subject);
            FileSystemResource file1 = new FileSystemResource(attachment1);
            FileSystemResource file2 = new FileSystemResource(attachment2);
            helper.addAttachment("noGst.pdf", file1);
            helper.addAttachment("onlyGst.pdf", file2);
        };
        try {
            mailSender.send(preparator);
            return true;
        } catch (MailException e) {
            log.error(e.getMessage());
        }
        return false;
    }
}
