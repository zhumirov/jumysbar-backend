package kz.btsd.edmarket.payment.check.model.auth;

import lombok.Data;

@Data
public class KassaCloseRequest {
    private Long idKkm;

    public KassaCloseRequest(Long idKkm) {
        this.idKkm = idKkm;
    }
}
