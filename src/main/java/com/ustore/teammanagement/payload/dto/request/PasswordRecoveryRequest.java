package com.ustore.teammanagement.payload.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordRecoveryRequest(
        @NotBlank(message = "O email é obrigatório")
        @Email
        String email
) {
}
