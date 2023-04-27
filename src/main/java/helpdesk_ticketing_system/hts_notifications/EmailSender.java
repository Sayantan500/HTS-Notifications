package helpdesk_ticketing_system.hts_notifications;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailSender {
    private final String USERNAME;
    private final String PASSWORD;
    private final String FROM;
    private final Session session;
    public EmailSender() {
        USERNAME = System.getenv("smtp_username");
        PASSWORD = System.getenv("smtp_password");
        String SMTP_HOST = System.getenv("smtp_host");
        String SMTP_PORT = System.getenv("smtp_port");
        FROM = System.getenv("from_email");
        String CONNECTION_TIMEOUT = System.getenv("connection_timeout");

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", true);
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", SMTP_PORT);
        prop.put("mail.smtp.ssl.trust", SMTP_HOST);
        prop.put("mail.smtp.connectiontimeout", CONNECTION_TIMEOUT);

        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

    }

    public void sendEmail(String recipient, String subject, String body) throws MessagingException {
        recipient = recipient + '@' + System.getenv("email_domain");
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(body, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
