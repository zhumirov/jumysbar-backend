package kz.btsd.edmarket.event.promocode;

import lombok.Data;

/**
 * Промокод
 */
@Data
public class PromocodeCheckResponse {
    private boolean confirmed;
    private Long price;

    public PromocodeCheckResponse() {
    }

    public PromocodeCheckResponse(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public PromocodeCheckResponse(boolean confirmed, Long price) {
        this.confirmed = confirmed;
        this.price = price;
    }
}
