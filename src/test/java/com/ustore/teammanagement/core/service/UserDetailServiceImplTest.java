package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.exceptions.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Test
    @DisplayName("Deve carregar o usuário quando o email existir")
    void shouldLoadUserByUsernameWhenUserExists() {

        String email = "test@example.com";

        Member member = new Member();
        member.setId(UUID.randomUUID());
        member.setEmail(email);
        member.setPassword("encoded-password");

        when(memberRepository.findByEmail(email))
                .thenReturn(Optional.of(member));

        UserDetails result = userDetailService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals("encoded-password", result.getPassword());

        verify(memberRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando o email não existir")
    void shouldThrowExceptionWhenUserDoesNotExist() {

        String email = "notfound@example.com";

        when(memberRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userDetailService.loadUserByUsername(email)
        );

        assertEquals("User not found with email: " + email, exception.getMessage());

        verify(memberRepository).findByEmail(email);
    }
}
