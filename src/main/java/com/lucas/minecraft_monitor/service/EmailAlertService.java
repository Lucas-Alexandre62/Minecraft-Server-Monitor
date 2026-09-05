package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(JavaMailSender.class)
public class EmailAlertService {

    private static final Logger logger =
            LoggerFactory.getLogger(EmailAlertService.class);

    private final JavaMailSender mailSender;

    @Value("${alert.email.from:}")
    private String fromAddress;

    @Value("${alert.email.to:}")
    private String toAddress;

    public EmailAlertService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void serverDown(MinecraftServer server) {
        sendEmail(
                "Servidor OFFLINE: " + server.getName(),
                "O servidor " + server.getName()
                        + " (" + server.getHost()
                        + ":" + server.getPort()
                        + ") está offline."
        );
    }

    public void serverUp(MinecraftServer server) {
        sendEmail(
                "Servidor ONLINE: " + server.getName(),
                "O servidor " + server.getName()
                        + " (" + server.getHost()
                        + ":" + server.getPort()
                        + ") está online novamente."
        );
    }

    private void sendEmail(String subject, String body) {
        if (fromAddress == null || fromAddress.isBlank()
                || toAddress == null || toAddress.isBlank()) {
            logger.warn(
                    "Email alertas não configurado: " +
                    "defina alert.email.from e alert.email.to"
            );
            return;
        }

        try {
            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setFrom(fromAddress);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);

            logger.info("Email de alerta enviado: {}", subject);
        } catch (MessagingException e) {
            logger.error(
                    "Erro ao enviar email de alerta: {}",
                    e.getMessage()
            );
        }
    }
}
