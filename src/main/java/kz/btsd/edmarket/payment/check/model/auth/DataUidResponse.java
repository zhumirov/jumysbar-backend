package kz.btsd.edmarket.payment.check.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataUidResponse {
    @JsonProperty("Uid")
    private String uid;
}
