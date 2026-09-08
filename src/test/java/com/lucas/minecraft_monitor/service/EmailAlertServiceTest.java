package com.lucas.minecraft_monitor.service;

import com.lucas.minecraft_monitor.model.MinecraftServer;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailAlertServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailAlertService emailAlertService;

    private MinecraftServer createServer() {
        MinecraftServer server = new MinecraftServer();
        ReflectionTestUtils.setField(server, "id", 1L);
        server.setName("Test Server");
        server.setHost("localhost");
        server.setPort(25565);
        return server;
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                emailAlertService, "fromAddress", "alert@example.com"
        );
        ReflectionTestUtils.setField(
                emailAlertService, "toAddress", "admin@example.com"
        );
    }

    @Test
    void serverDownSendsEmail() throws Exception {
        MinecraftServer server = createServer();
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailAlertService.serverDown(server);

        ArgumentCaptor<MimeMessage> captor =
                ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        verify(mailSender).createMimeMessage();
    }

    @Test
    void serverUpSendsEmail() throws Exception {
        MinecraftServer server = createServer();
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailAlertService.serverUp(server);

        ArgumentCaptor<MimeMessage> captor =
                ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        verify(mailSender).createMimeMessage();
    }

    @Test
    void doesNotSendWhenFromAddressIsBlank() {
        MinecraftServer server = createServer();
        ReflectionTestUtils.setField(
                emailAlertService, "fromAddress", ""
        );

        emailAlertService.serverDown(server);

        verifyNoInteractions(mailSender);
    }

    @Test
    void doesNotSendWhenToAddressIsBlank() {
        MinecraftServer server = createServer();
        ReflectionTestUtils.setField(
                emailAlertService, "toAddress", ""
        );

        emailAlertService.serverDown(server);

        verifyNoInteractions(mailSender);
    }

    @Test
    void doesNotSendWhenBothAddressesAreNull() {
        MinecraftServer server = createServer();
        ReflectionTestUtils.setField(
                emailAlertService, "fromAddress", null
        );
        ReflectionTestUtils.setField(
                emailAlertService, "toAddress", null
        );

        emailAlertService.serverDown(server);
        emailAlertService.serverUp(server);

        verifyNoInteractions(mailSender);
    }
}
