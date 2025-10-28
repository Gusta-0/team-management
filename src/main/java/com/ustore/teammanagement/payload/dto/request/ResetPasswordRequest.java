package com.ustore.teammanagement.payload.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(@NotBlank(message = "Token é obrigatório")
                                    String token,

                                   @NotBlank(message = "Senha é obrigatória")
                                   @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
                                   @Pattern(
                                           regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/~`]).+$",
                                           message = "A senha deve conter letra maiúscula, minúscula, número e caractere especial"
                                   )
                                    String newPassword) {
}
