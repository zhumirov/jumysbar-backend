package kz.btsd.edmarket.webkassa.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.btsd.edmarket.payment.check.model.auth.KassaUser;
import lombok.Data;

@Data
public class DataWebKassaResponse {
    @JsonProperty("Token")
    private String token;
}
