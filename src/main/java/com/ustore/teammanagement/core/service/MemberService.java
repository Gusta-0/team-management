package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.Specifications.MemberSpecification;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Role;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.exceptions.ConflictException;
import com.ustore.teammanagement.exceptions.ResourceNotFoundException;
import com.ustore.teammanagement.payload.dto.request.MemberRequest;
import com.ustore.teammanagement.payload.dto.request.MemberUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.MemberResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void emailExiste(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email " + email + " já cadastrado!");
        }
    }

    @Transactional
    public MemberResponse saveMember(MemberRequest memberRequest) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated()) {
//            throw new AccessDeniedException("Acesso negado: usuário não autenticado.");
//        }
//
//        String emailLogado = auth.getName();
//        var memberLogado = memberRepository.findByEmail(emailLogado)
//                .orElseThrow(() -> new ResourceNotFoundException("Membro logado não encontrado"));
//
//        if (!(memberLogado.getRole().equals(Role.ADMIN) || memberLogado.getRole().equals(Role.MANAGER))) {
//            throw new AccessDeniedException("Acesso negado: apenas ADMIN ou MANAGER podem criar usuários.");
//        }

        emailExiste(memberRequest.email());

        var member = memberRequest.toMember();
        member.setStatus(MemberStatus.ACTIVE);
        member.setPassword(passwordEncoder.encode(memberRequest.password()));

        var savedMember = memberRepository.save(member);
        return new MemberResponse(savedMember);
    }


    public Page<MemberResponse> search(String name, String email, Pageable pageable) {
        return memberRepository.findAll(
                MemberSpecification.withSearch(name, email),
                pageable
        ).map(MemberResponse::new);
    }

    public Page<MemberResponse> filter(String department, MemberStatus status, Role role, Pageable pageable) {
        return memberRepository.findAll(
                MemberSpecification.withFilters(department, status, role),
                pageable
        ).map(MemberResponse::new);
    }

    @Transactional
    public MemberResponse update(UUID id, MemberUpdateRequest memberUpdateRequest) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailLogado = auth.getName();

        var memberLogado = memberRepository.findByEmail(emailLogado).orElseThrow(() -> new ResourceNotFoundException("Membro logado não encontrado"));
        var targetMember = memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Membro alvo não encontrado"));

        if (!(memberLogado.getRole().equals(Role.ADMIN) || memberLogado.getRole().equals(Role.MANAGER))
                        && !targetMember.getId().equals(memberLogado.getId())
        ) {throw new AccessDeniedException("Acesso negado: você não tem permissão para atualizar este membro.");
        }
        memberUpdateRequest.updateMember(targetMember, memberUpdateRequest);

        var updatedMember = memberRepository.save(targetMember);
        return new MemberResponse(updatedMember);
    }

    @Transactional
    public void inactivateMember(UUID id) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailLogado = auth.getName();

        var memberLogado = memberRepository.findByEmail(emailLogado).orElseThrow(() -> new ResourceNotFoundException("Membro logado não encontrado"));
        var targetMember = memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Membro alvo não encontrado"));

        if (!(memberLogado.getRole().equals(Role.ADMIN) || memberLogado.getRole().equals(Role.MANAGER))
                && !targetMember.getId().equals(memberLogado.getId())) {
            throw new AccessDeniedException("Acesso negado: você não tem permissão para inativar este membro.");
        }

        targetMember.setStatus(MemberStatus.INACTIVE);
        memberRepository.save(targetMember);
    }
}
