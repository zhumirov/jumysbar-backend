package kz.btsd.edmarket.user.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SignupOrgCheckDto {

    // todo rename to verificationId
    @NotNull
    private Long registrationId;

    @NotNull
    private Long smsCode;

    @NotNull
    @Size(min = 8)
    private String password;

    private Platform platform = Platform.JUMYSBAR;

    private String name;
    private String email;

    private String bitrixPage;
}
