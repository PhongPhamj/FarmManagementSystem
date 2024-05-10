package com.fpt.fms.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityUtilsTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testGetCurrentUserLoginWhenAuthenticationIsProperlySetUpThenReturnCorrectLogin() {
        String expectedLogin = "testUser";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(expectedLogin);

        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();

        assertTrue(actualLogin.isPresent());
        assertEquals(expectedLogin, actualLogin.get());
    }

    @Test
    public void testGetCurrentUserLoginWhenAuthenticationIsNullThenReturnEmptyOptional() {
        when(securityContext.getAuthentication()).thenReturn(null);

        Optional<String> actualLogin = SecurityUtils.getCurrentUserLogin();

        assertTrue(actualLogin.isEmpty());
    }
}
