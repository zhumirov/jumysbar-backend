package kz.btsd.edmarket.security.verification.model;

import lombok.Data;

@Data
public class VerificationCreatedDto {
    private Long id;
    public VerificationCreatedDto(Long id) {
        this.id = id;
    }
}
