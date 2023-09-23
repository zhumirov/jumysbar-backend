package kz.btsd.edmarket.payment.check.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KassaOpenShiftResponse {
    @JsonProperty("Status")
    private Long status;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Data")
    private DataOpenShiftResponse data;

}
