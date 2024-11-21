package com.dattran.practice.app.responses;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    Long id;

    String username;

    String address;

    Timestamp dateOfBirth;

    String email;

    String phoneNumber;

    List<RoleResponse> roles;
}
