package com.fpt.fms.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.fpt.fms.service.MailService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NoOpMailConfiguration {

    private final MailService mockMailService;

    public NoOpMailConfiguration() {
        mockMailService = mock(MailService.class);
        doNothing().when(mockMailService).sendActivationEmail(any(), any());
    }

    @Bean
    public MailService mailService() {
        MailService mockMailService = Mockito.mock(MailService.class);
        Mockito.doNothing().when(mockMailService).sendActivationEmail(Mockito.any(), Mockito.any());
        return mockMailService;
    }
}
