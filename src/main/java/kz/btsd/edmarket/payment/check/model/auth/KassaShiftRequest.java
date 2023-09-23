package kz.btsd.edmarket.payment.check.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KassaShiftRequest {
    @JsonProperty("IdKkm")
    private Long idKkm;

    public KassaShiftRequest(Long idKkm) {
        this.idKkm = idKkm;
    }
}
