package kz.btsd.edmarket.user.model;

import lombok.Data;

@Data
public class AppleAuthRequest {
    private String authenticationCode;
    private String email;
    private String userIdentifier;
    private String identityToken;
    private String givenName;
    private String familyName;
}

