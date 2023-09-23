package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserCheckDto {

    @NotNull
    private Long registrationId;

    @NotNull
    private Long smsCode;
}
