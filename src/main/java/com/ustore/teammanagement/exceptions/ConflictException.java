package com.ustore.teammanagement.exceptions;

public class ConflictException extends RuntimeException {
    public ConflictException(String mensagem){
        super(mensagem);
    }
}
