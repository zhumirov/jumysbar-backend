package kz.btsd.edmarket.payment.check.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataKassaResponse {
    @JsonProperty("Token")
    private String token;
    @JsonProperty("User")
    private KassaUser user;
}
