package com.Smart.Service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
    public boolean SendEmail(String subject, String message, String to) throws MessagingException {
        boolean fl = false;
        String from = "singhbd844@gmail.com";
        String host = "smtp.gmail.com";

// getting system properties
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES" + properties);
        // setting important info of host

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("singhbd844@gmail.com","qtgkdekhvoipagvt");
            }
        });

        session.setDebug(true);


        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // Set recipient's address

            mimeMessage.setSubject(subject);
//            mimeMessage.setText(message);
            mimeMessage.setContent(message,"text/html");
            Transport.send(mimeMessage);
            System.out.println("Email sent successfully");
            fl = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fl;
    }





}
