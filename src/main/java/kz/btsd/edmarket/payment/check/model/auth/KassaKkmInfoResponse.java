package kz.btsd.edmarket.payment.check.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KassaKkmInfoResponse {
    @JsonProperty("Status")
    private Long status;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Data")
    private DataKkmResponse data;

    public KassaKkmInfoResponse() {
    }
}
