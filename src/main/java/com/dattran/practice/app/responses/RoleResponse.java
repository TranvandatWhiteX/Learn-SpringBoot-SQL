package com.dattran.practice.app.responses;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class RoleResponse {
    Long roleId;

    String roleName;

    List<PermissionResponse> permissions;
}
