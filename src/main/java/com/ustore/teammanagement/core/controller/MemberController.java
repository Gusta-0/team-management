package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.config.MemberAPI;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Role;
import com.ustore.teammanagement.core.service.MemberService;
import com.ustore.teammanagement.payload.dto.request.MemberRequest;
import com.ustore.teammanagement.payload.dto.request.MemberUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.MemberResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/member")
@Validated
public class MemberController implements MemberAPI {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MemberResponse> saveMember(@Valid @RequestBody MemberRequest memberRequest) {
        MemberResponse response = memberService.saveMember(memberRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/search")
    public Page<MemberResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @ParameterObject Pageable pageable
    ) {
        return memberService.search(name, email, pageable);
    }

    @GetMapping("/filter")
    public Page<MemberResponse> filter(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(required = false) Role role,
            @ParameterObject Pageable pageable
    ) {
        return memberService.filter(department, status, role, pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable UUID id,
            @Valid @RequestBody MemberUpdateRequest dto
    ) throws AccessDeniedException {
        MemberResponse updated = memberService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void inactivate(@PathVariable UUID id) throws AccessDeniedException {
        memberService.inactivateMember(id);
    }
}