package core.utils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/*
    This approach is awkward. In a future release, Dashboard is use GMail Authentication
    with the user's student mail. And use that for feedback instead.
 */
public class Mailer {
    private String subject;
    private String content;
    private String senderMail;
    private String senderPsswd;
    public static final String DEVELOPER_MAIL = "utgdashboard@gmail.com";
    public static final String DEVELOPER_MAIL_PSSWD = "wdrammeh20";


    public Mailer(String subject, String content){
        this.subject = subject;
        this.content = content;
        senderMail = DEVELOPER_MAIL;
        senderPsswd = DEVELOPER_MAIL_PSSWD;
    }

    public boolean send() {
        final String host = "smtp.gmail.com";
        final Properties properties = System.getProperties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", senderMail);
        properties.put("mail.smtp.password", senderPsswd);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        final Session session = Session.getDefaultInstance(properties);
        try {
            final MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderMail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(DEVELOPER_MAIL));
            message.setSubject(subject);
            message.setText(content);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, senderMail, senderPsswd);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            App.reportInfo("Successful","The message was sent successfully. Thank you for the feedback.");
            return true;
        } catch (MessagingException mex) {
            App.reportError("Error","An error encountered while sending the feedback.\n" +
                    "Try again later.");
            return false;
        }
    }

}
