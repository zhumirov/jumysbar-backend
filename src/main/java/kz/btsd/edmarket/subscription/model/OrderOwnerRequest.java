package kz.btsd.edmarket.subscription.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderOwnerRequest {
    @NotNull
    private Long userId;
}
