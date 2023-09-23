package kz.btsd.edmarket.event.promocode;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Промокод
 */
@Data
public class PromocodeCheckRequest {
    @NotNull
    private Long eventId;
    private Long planId;
    //Название промокода
    @NotNull
    private String title;

    public PromocodeCheckRequest() {
    }
}
