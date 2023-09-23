package kz.btsd.edmarket.security.apple;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AppleTokenPayload {
    private String iss;
    private String aud;
    private String iat;
    private Integer exp;
    private String sub;

    @JsonProperty("c_hash")
    private String cHash;
    private String email;

    @JsonProperty("auth_time")
    private Integer authTime;

    @JsonProperty("is_private_email")
    private Boolean isPrivateEmail;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("nonce_supported")
    private String nonceSupported;
}
