package com.ustore.teammanagement.core.repository;
import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.payload.dto.request.LoginRequest;

public interface AuthenticationService {
    Member authenticate(LoginRequest request);
}
