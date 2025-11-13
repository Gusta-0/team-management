package com.ustore.teammanagement.payload.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(@Email(message = "Formato de email inválido")
                                    @NotBlank(message = "Email é obrigatório")
                                    String email) {
}
