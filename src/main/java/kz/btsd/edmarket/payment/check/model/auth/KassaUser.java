package kz.btsd.edmarket.payment.check.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KassaUser {
    @JsonProperty("Id")
    private Long id;
    @JsonProperty("PhoneLogin")
    private String phoneLogin;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Lock")
    private boolean lock;
    @JsonProperty("IdShift")
    private Long idShift;
}
