package kz.btsd.edmarket.payment.check.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KassaShiftResponse {
    @JsonProperty("Status")
    private Long status;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Data")
    private DataShiftResponse data;

}
