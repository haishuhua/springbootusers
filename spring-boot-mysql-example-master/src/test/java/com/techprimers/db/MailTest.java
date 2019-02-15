package com.techprimers.db;

import com.techprimers.db.services.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

    @Autowired
    MailService mailService;

    public static String mailTo = "cxy0224@gmail.com";

    @Test
    public void sendSimpleMail() throws Exception {
        mailService.sendmail(mailTo,"SUNNY");
    }

    @Test
    public void sendHTMLMail() throws MessagingException {
        mailService.sendHTMLMail(mailTo,"SUNNY");
    }

    @Test
    public void sendTemplateMail() throws Exception {

        mailService.sendTemplateMail(mailTo,"SUNNY");


    }


}
