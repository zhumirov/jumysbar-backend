package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResetUserEmailCheckDto {

    @NotNull
    private Long registrationId;

    @Email
    @NotNull
    private String email;

    @NotNull
    private Long smsCode;

    @Size(min = 8)
    @NotNull
    private String password;
}
