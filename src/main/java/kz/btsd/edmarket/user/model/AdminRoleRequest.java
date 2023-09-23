package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminRoleRequest {
    @NotNull
    private UserRole userRole;
    @NotNull
    private Long userId;
}
