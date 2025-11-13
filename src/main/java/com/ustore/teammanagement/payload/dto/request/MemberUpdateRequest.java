package com.ustore.teammanagement.payload.dto.request;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String name,

        @Email(message = "Email deve ser válido")
        String email,

        Role role,

        String department,

        @Pattern(
                regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
                message = "Telefone inválido. Use o formato (XX) XXXXX-XXXX"
        )
        @Size(min = 10, max = 15, message = "Telefone deve ter entre 10 e 15 caracteres")
        String phone,

        MemberStatus status,

        String image
) {

    public void updateMember(Member member, MemberUpdateRequest updateRequest) {
        if (updateRequest.name() != null) {
            member.setName(updateRequest.name());
        }
        if (updateRequest.email() != null) {
            member.setEmail(updateRequest.email());
        }

        if (updateRequest.role() != null) {
            member.setRole(updateRequest.role());
        }
        if (updateRequest.department() != null) {
            member.setDepartment(updateRequest.department());
        }
        if (updateRequest.phone() != null) {
            member.setPhone(updateRequest.phone());
        }
        if (updateRequest.status() != null) {
            member.setStatus(updateRequest.status());
        }
        if (updateRequest.image() != null) {
            member.setImage(updateRequest.image());
        }
    }
}
