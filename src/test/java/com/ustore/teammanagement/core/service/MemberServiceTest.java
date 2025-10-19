package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.enums.MemberStatus;
import com.ustore.teammanagement.enums.Role;
import com.ustore.teammanagement.exception.ConflictException;
import com.ustore.teammanagement.payload.dto.request.MemberRequest;
import com.ustore.teammanagement.payload.dto.request.MemberUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.MemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    Member member1;
    Member member2;
    MemberRequest memberRequest;

    @BeforeEach
    public void setUp() {
        member1 = Member.builder()
                .name("Carlos Silva")
                .email("carlos.silva@empresa.com")
                .password("AAAaaa334444443..")
                .role(com.ustore.teammanagement.enums.Role.MEMBER)
                .department("TI")
                .phone("(83) 015401183")
                .build();

        member2 = Member.builder()
                .name("Maria Souza")
                .email("maria.souza@empresa.com")
                .role(com.ustore.teammanagement.enums.Role.ADMIN)
                .department("RH")
                .phone("(83) 010203040")
                .status(MemberStatus.ACTIVE)
                .build();

        memberRequest = new MemberRequest(
                "Carlos Silva",
                "carlos.silva@empresa.com",
                "AAAaaa334444443..",
                "ADMIN",
                "TI",
                "(83) 015401183",
                "image_url"
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(memberRepository.findByEmail(memberRequest.email()))
                .thenReturn(Optional.of(member1));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> memberService.saveMember(memberRequest)
        );

        assertEquals("Email " + memberRequest.email() + " já cadastrado!", exception.getMessage());

        verify(memberRepository, never()).save(any(Member.class));
    }


    @Test
    void shouldNotThrowExceptionWhenEmailDoesNotExist() {
        when(memberRepository.findByEmail(member1.getEmail())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> memberService.emailExiste(member1.getEmail()));
        verify(memberRepository, times(1)).findByEmail(member1.getEmail());
    }

    @Test
    void shouldSaveMemberSuccessfully() {
        when(memberRepository.findByEmail(memberRequest.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(memberRequest.password()))
                .thenReturn("encoded123");

        when(memberRepository.save(any(Member.class)))
                .thenReturn(member1);

        MemberResponse response = memberService.saveMember(memberRequest);

        assertNotNull(response);
        assertEquals("Carlos Silva", response.name());
        assertEquals("carlos.silva@empresa.com", response.email());
        assertEquals(MemberStatus.ACTIVE, response.status());

        verify(memberRepository, times(1)).findByEmail(memberRequest.email());
        verify(passwordEncoder, times(1)).encode(memberRequest.password());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void shouldReturnPagedMembersSuccessfully() {
        List<Member> members = List.of(member1, member2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> page = new PageImpl<>(members, pageable, members.size());

        when(memberRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        Page<MemberResponse> result = memberService.search("Carlos", "empresa.com", pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Carlos Silva", result.getContent().get(0).name());
        assertEquals("Maria Souza", result.getContent().get(1).name());

        verify(memberRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldReturnFilteredPageMembersSuccessfully (){
        List<Member> members = List.of(member1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Member> page = new PageImpl<>(members, pageable, members.size());

        when(memberRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        Page<MemberResponse> result = memberService.filter("TI", MemberStatus.ACTIVE, Role.MEMBER , pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("TI", result.getContent().get(0).department());
        assertEquals(MemberStatus.ACTIVE, result.getContent().get(0).status());
        assertEquals(Role.MEMBER, result.getContent().get(0).role());

        verify(memberRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldUpdateMemberSuccessfullyAsAdmin() throws Exception {
        UUID targetId = UUID.randomUUID();

        Member memberLogado = Member.builder()
                .id(UUID.randomUUID())
                .name("Admin")
                .email("admin@teste.com")
                .role(Role.ADMIN)
                .build();

        Member targetMember = Member.builder()
                .id(targetId)
                .name("Carlos")
                .email("carlos@teste.com")
                .role(Role.MEMBER)
                .build();

        MemberUpdateRequest updateRequest = mock(MemberUpdateRequest.class);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin@teste.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(memberRepository.findByEmail("admin@teste.com")).thenReturn(Optional.of(memberLogado));
        when(memberRepository.findById(targetId)).thenReturn(Optional.of(targetMember));
        when(memberRepository.save(targetMember)).thenReturn(targetMember);

        doNothing().when(updateRequest).updateMember(targetMember, updateRequest);

        MemberResponse response = memberService.update(targetId, updateRequest);

        assertNotNull(response);
        assertEquals("Carlos", response.name());
        verify(updateRequest, times(1)).updateMember(targetMember, updateRequest);
        verify(memberRepository, times(1)).save(targetMember);
    }

}