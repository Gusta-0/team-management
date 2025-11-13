package com.ustore.teammanagement.exceptions;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(Long id) {
        super("Usuário com ID " + id + " não encontrado");
    }
}

