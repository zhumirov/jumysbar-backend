package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserPasswordResetRequest {
    @NotNull
    private Long userId;
    @NotNull
    @Size(min = 8)
    private String password;
}
