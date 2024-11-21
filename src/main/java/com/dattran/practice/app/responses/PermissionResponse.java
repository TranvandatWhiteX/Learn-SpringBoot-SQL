package com.dattran.practice.app.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionResponse {
    String permissionId;

    String description;
}
