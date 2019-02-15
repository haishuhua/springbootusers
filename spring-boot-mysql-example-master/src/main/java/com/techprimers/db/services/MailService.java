
package com.techprimers.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public static String MAIL_FROM = "weatheralertawesome@gmail.com";
    public static String MAIL_TO = "cxy0224@gmail.com";

    public void sendmail(String mailTo, String weather) {
        System.out.println("HERE");
        try {
            System.out.println("HERE");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MAIL_FROM);
            message.setTo(mailTo);
            message.setSubject("Today's Weather");
            message.setText(weather);
            mailSender.send(message);
            System.out.println(message.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void sendHTMLMail(String mailTo, String weather) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(MAIL_FROM);
        helper.setTo(mailTo);
        helper.setSubject("Today's Weather");
        helper.setText("<html><body><h1>" + weather + " </h1></body></html>", true);

        mailSender.send(mimeMessage);

    }

    public void sendTemplateMail(String mailTo, String sunny) {

    }
}