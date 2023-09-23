package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class LoginByPasswordRequest {

    @NotEmpty
    private String phone;

    @NotEmpty
    @Size(min = 8)
    private String password;
}
