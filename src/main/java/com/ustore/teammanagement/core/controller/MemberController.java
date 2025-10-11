package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.core.service.MemberService;
import com.ustore.teammanagement.enums.MemberStatus;
import com.ustore.teammanagement.enums.Role;
import com.ustore.teammanagement.payload.dto.request.MemberRequest;
import com.ustore.teammanagement.payload.dto.request.MemberUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.MemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Validated
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN' or hasRole('MANAGER'))")
    public ResponseEntity<MemberResponse> saveMember(@Valid @RequestBody MemberRequest memberRequest) {
        return ResponseEntity.ok(memberService.saveMember(memberRequest));
    }

    @GetMapping("/member/search")
    public Page<MemberResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @ParameterObject Pageable pageable
    ) {
        return memberService.Search(name, email, pageable);
    }

    @GetMapping("/member/filter")
    public Page<MemberResponse> filtrar(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(required = false) Role role,
            @ParameterObject Pageable pageable
    ) {
        return memberService.filter(department, status, role, pageable);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable UUID id,
            @RequestBody @Valid MemberUpdateRequest dto
    ) throws AccessDeniedException {
        MemberResponse update = memberService.update(id, dto);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN' or hasRole('MANAGER'))")
    public void inactivate(@PathVariable UUID id) throws AccessDeniedException {
        memberService.inactivateMember(id);
    }
}
