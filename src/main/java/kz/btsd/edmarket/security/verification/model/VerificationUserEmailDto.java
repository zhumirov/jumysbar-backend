package kz.btsd.edmarket.security.verification.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class VerificationUserEmailDto {
    @Email
    @NotNull
    private String email;
}
