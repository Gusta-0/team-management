package com.ustore.teammanagement.config;

import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Role;
import com.ustore.teammanagement.payload.dto.request.MemberRequest;
import com.ustore.teammanagement.payload.dto.request.MemberUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Tag(name = "Members", description = "Endpoints for managing system members")
public interface MemberAPI {

    @Operation(
            summary = "Create a new member",
            description = "Adds a new member to the team. Only ADMIN and MANAGER roles are allowed."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member successfully created",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already registered",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<MemberResponse> saveMember(
            @Valid @RequestBody MemberRequest memberRequest
    );

    @Operation(
            summary = "Search members",
            description = "Searches for members by name and/or email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of members found")
    })
    Page<MemberResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Filter members",
            description = "Filters members by department, status, and access level."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtered list of members found")
    })
    Page<MemberResponse> filter(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(required = false) Role role,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Update member information",
            description = "Updates a member's information. Regular users can update only their own data. "
                    + "Admins and Managers can update any member."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member successfully updated",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<MemberResponse> updateMember(
            @Parameter(description = "ID of the member to be updated", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody MemberUpdateRequest dto
    ) throws AccessDeniedException;

    @Operation(
            summary = "Inactivate a member",
            description = "Logically inactivates a member. Only Admins and Managers have permission for this action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member successfully inactivated"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    void inactivate(
            @Parameter(description = "ID of the member to inactivate", required = true)
            @PathVariable UUID id
    ) throws AccessDeniedException;
}
