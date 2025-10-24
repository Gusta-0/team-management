package com.ustore.teammanagement.payload.dto.request;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 8, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/~`]).+$",
                message = "A senha deve conter letra maiúscula, minúscula, número e caractere especial"
        )
        String password,

        String role,

        @NotBlank(message = "Departamento é obrigatório")
        String department,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(
                regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
                message = "Telefone inválido. Use o formato (XX) XXXXX-XXXX"
        )
        @Size(min = 10, max = 15, message = "Telefone deve ter entre 10 e 15 caracteres")
        String phone,

        String image
) {
    public MemberRequest(Member member) {
        this(
                member.getName(),
                member.getEmail(),
                member.getPassword(),
                member.getRole().name(),
                member.getDepartment(),
                member.getPhone(),
                member.getImage()
        );
    }

    public Member toMember() {
        return Member.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .role(this.role != null ? Enum.valueOf(Role.class, this.role) : Role.MEMBER)
                .department(this.department)
                .phone(this.phone)
                .image(this.image)
                .build();
    }
}
