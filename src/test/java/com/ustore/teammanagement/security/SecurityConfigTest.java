package com.ustore.teammanagement.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void shouldReturnPasswordEncodeCorrectly(){
        var encoder = securityConfig.bCryptPasswordEncoder();
        String myPassword = "myPassword";
        String encodedPassword = encoder.encode(myPassword);

        assertNotEquals(myPassword, encodedPassword);
        assertTrue(encoder.matches(myPassword, encodedPassword));
    }

    @Test
    void shouldReturnAuthenticationManager() throws Exception {
        var mockConfig = mock(AuthenticationConfiguration.class);
        var mockManager = mock(AuthenticationManager.class);

        when(mockConfig.getAuthenticationManager()).thenReturn(mockManager);
        var authenticationManager = this.securityConfig.authenticationManager(mockConfig);

        assertEquals(mockManager, authenticationManager);
        verify(mockConfig, times(1)).getAuthenticationManager();
    }
}