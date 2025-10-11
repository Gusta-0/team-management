package com.ustore.teammanagement.payload.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "O token é obrigatório")
        String token,

        @NotBlank(message = "A nova senha é obrigatória")
        String newPassword,

        @NotBlank(message = "A confirmação de senha é obrigatória")
        String confirmPassword
) {
}
