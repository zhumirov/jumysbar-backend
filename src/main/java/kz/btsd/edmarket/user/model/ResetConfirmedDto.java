package kz.btsd.edmarket.user.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResetConfirmedDto {
    private boolean confirmed;

    public ResetConfirmedDto(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
