package API;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class SendEmailReport {

    public static void sendReport() {

        final String fromEmail = "simarpreet.singh@signitysolutions.com";
        final String password = "oledwogvrvebbvud";
        final String toEmail = "simarpreet.singh@signitysolutions.com,palak.singla@signitysolutions.in,ashwani@signitysolutions.com,sanjeev@signitysolutions.in";

        // palak.singla@signitysolutions.in,
        // ashwani@signitysolutions.com,
        // ,sanjeev@signitysolutions.in

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(fromEmail));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            message.setSubject("Automation Test Execution Report");

            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setText(
                    "Hi,\n\nPlease find the attached FIntel automation execution report.\n\nRegards,\nSimarpreet Singh"
            );

            MimeBodyPart attachmentPart = new MimeBodyPart();

            String reportPath = System.getProperty("user.dir")
                    + "/reports/ExtentReport.html";

            attachmentPart.attachFile(new File(reportPath));

            Multipart multipart = new MimeMultipart();

            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email sent successfully with Extent Report");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}