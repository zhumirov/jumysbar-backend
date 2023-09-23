package kz.btsd.edmarket.online.subscription;

import lombok.Data;

@Data
public class OnlineRequestDto {
    private Long userId;
    private Integer from;
    private Integer size;

    public OnlineRequestDto() {
    }
}
