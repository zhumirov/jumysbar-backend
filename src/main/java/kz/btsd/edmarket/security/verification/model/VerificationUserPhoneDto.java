package kz.btsd.edmarket.security.verification.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class VerificationUserPhoneDto {
    @NotNull
    @Size(min = 10, max = 15)
    private String phone;
}
