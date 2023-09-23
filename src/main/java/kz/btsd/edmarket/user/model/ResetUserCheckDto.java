package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResetUserCheckDto {

    @NotNull
    private Long registrationId;

    @NotNull
    private Long smsCode;

    @Size(min = 8)
    @NotNull
    private String password;
}
