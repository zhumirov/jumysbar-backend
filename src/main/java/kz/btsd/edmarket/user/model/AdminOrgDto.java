package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AdminOrgDto {

    @NotNull
    private Long registrationId;

    @Size(min = 10, max = 15)
    @NotNull
    private String phone;

    @NotNull
    private Long smsCode;

    @Size(min = 8)
    @NotNull
    private String password;
}
