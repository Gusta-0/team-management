package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Role;
import com.ustore.teammanagement.core.service.MemberService;
import com.ustore.teammanagement.payload.dto.request.MemberRequest;
import com.ustore.teammanagement.payload.dto.request.MemberUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.MemberResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    private MemberController memberController;

    @Mock
    private MemberService memberService;

    @Test
    void shouldSaveMemberSuccessfully() {
        MemberRequest request = new MemberRequest(
                "Gustavo Barbosa",
                "gustavo@test.com",
                "SenhaForte@123",
                "ADMIN",
                "TI",
                "(81) 99999-9999",
                "image.png"
        );

        MemberResponse response = new MemberResponse(
                UUID.randomUUID(),
                "Gustavo Barbosa",
                "gustavo@test.com",
                Role.ADMIN,
                "TI",
                "(81) 99999-9999",
                OffsetDateTime.now(),
                MemberStatus.ACTIVE,
                "image.png"
        );

        when(memberService.saveMember(request)).thenReturn(response);

        ResponseEntity<MemberResponse> result = memberController.saveMember(request);

        assertEquals(HttpStatus.CREATED.value(), result.getStatusCode().value());
        assertNotNull(result.getBody());

        MemberResponse body = result.getBody();

        assertEquals("Gustavo Barbosa", body.name());
        assertEquals("gustavo@test.com", body.email());
        assertEquals(Role.ADMIN, body.role());
        assertEquals("TI", body.department());
        assertEquals("(81) 99999-9999", body.phone());
        assertEquals("image.png", body.image());
        assertEquals(MemberStatus.ACTIVE, body.status());

        verify(memberService).saveMember(request);
    }

    @Test
    void shouldSearchMembersSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);

        MemberResponse member = new MemberResponse(
                UUID.randomUUID(),
                "John Doe",
                "john@test.com",
                Role.ADMIN,
                "TI",
                "(81) 99999-9999",
                OffsetDateTime.now(),
                MemberStatus.ACTIVE,
                null
        );

        Page<MemberResponse> page = new PageImpl<>(List.of(member));

        when(memberService.search("John", "john@test.com", pageable))
                .thenReturn(page);

        Page<MemberResponse> result =
                memberController.search("John", "john@test.com", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).name());

        verify(memberService).search("John", "john@test.com", pageable);
    }

    @Test
    void shouldFilterMembersSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);

        MemberResponse member = new MemberResponse(
                UUID.randomUUID(),
                "Alice Silva",
                "alice@test.com",
                Role.MANAGER,
                "Financeiro",
                "(81) 98888-7777",
                OffsetDateTime.now(),
                MemberStatus.ACTIVE,
                null
        );

        Page<MemberResponse> page = new PageImpl<>(List.of(member));

        when(memberService.filter("Financeiro", MemberStatus.ACTIVE, Role.MANAGER, pageable))
                .thenReturn(page);

        Page<MemberResponse> result =
                memberController.filter("Financeiro", MemberStatus.ACTIVE, Role.MANAGER, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Alice Silva", result.getContent().get(0).name());
        assertEquals("Financeiro", result.getContent().get(0).department());

        verify(memberService).filter("Financeiro", MemberStatus.ACTIVE, Role.MANAGER, pageable);
    }

    @Test
    void shouldUpdateMemberSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        MemberUpdateRequest dto = new MemberUpdateRequest(
                "John Updated",
                "updated@test.com",
                Role.MANAGER,
                "Financeiro",
                "(81) 99999-9999",
                MemberStatus.ACTIVE,
                null
        );

        MemberResponse response = new MemberResponse(
                id,
                "John Updated",
                "updated@test.com",
                Role.MANAGER,
                "Financeiro",
                "(81) 99999-9999",
                OffsetDateTime.now(),
                MemberStatus.ACTIVE,
                null
        );

        when(memberService.update(id, dto)).thenReturn(response);

        ResponseEntity<MemberResponse> result = memberController.updateMember(id, dto);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("John Updated", result.getBody().name());
        assertEquals("updated@test.com", result.getBody().email());
        assertEquals(Role.MANAGER, result.getBody().role());
        assertEquals("Financeiro", result.getBody().department());

        verify(memberService).update(id, dto);
    }

    @Test
    void shouldDenyAccessWhenUpdatingAnotherMember() throws Exception {
        UUID id = UUID.randomUUID();

        MemberUpdateRequest dto = new MemberUpdateRequest(
                "John Doe",
                "john@test.com",
                Role.MEMBER,
                "TI",
                "(11) 98888-7777",
                MemberStatus.ACTIVE,
                null
        );

        when(memberService.update(id, dto))
                .thenThrow(new AccessDeniedException(
                        "Acesso negado: você não tem permissão para atualizar este membro."
                ));

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> memberController.updateMember(id, dto)
        );

        assertEquals("Acesso negado: você não tem permissão para atualizar este membro.",
                ex.getMessage());

        verify(memberService).update(id, dto);
    }

    @Test
    void shouldInactivateMember_whenUserIsAdminOrManager() throws Exception {
        UUID memberId = UUID.randomUUID();

        doNothing().when(memberService).inactivateMember(memberId);

        assertDoesNotThrow(() -> memberController.inactivate(memberId));

        verify(memberService, times(1)).inactivateMember(memberId);
    }

    @Test
    void shouldThrowAccessDenied_whenMemberTriesToInactivateAnotherMember() throws Exception {
        UUID memberId = UUID.randomUUID();

        doThrow(new AccessDeniedException("Acesso negado: você não tem permissão para inativar este membro."))
                .when(memberService).inactivateMember(memberId);

        Exception exception = assertThrows(
                AccessDeniedException.class,
                () -> memberController.inactivate(memberId)
        );

        assertEquals("Acesso negado: você não tem permissão para inativar este membro.", exception.getMessage());
        verify(memberService, times(1)).inactivateMember(memberId);
    }
}


