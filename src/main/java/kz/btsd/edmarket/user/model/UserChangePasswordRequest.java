package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserChangePasswordRequest {
    private String password;

    @NotNull
    @Size(min = 8)
    private String newPassword;
}
