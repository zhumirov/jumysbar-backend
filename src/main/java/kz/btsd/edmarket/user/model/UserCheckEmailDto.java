package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserCheckEmailDto {

    @NotNull
    private Long registrationId;

    @Email
    @NotNull
    private String email;

    @NotNull
    private Long smsCode;
}
