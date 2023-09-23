package kz.btsd.edmarket.security.verification.model;

import kz.btsd.edmarket.user.model.WithPhone;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class VerificationUserDto implements WithPhone {
    @Size(min = 10, max = 15)
    private String phone;
    @Email
    private String email;
}
