package kz.btsd.edmarket.user.model;

import lombok.Data;

@Data
public class SignupDto {
    private boolean confirmed;
    private Long userId; //todo нужен ли вообше userId?
    private UserDto user;
    private String accessToken;

    public SignupDto(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public SignupDto(boolean confirmed, UserDto user, Long userId) {
        this.confirmed = confirmed;
        this.userId = userId;
        this.user = user;
    }

    public SignupDto(boolean confirmed, UserDto user, Long userId, String accessToken) {
        this.confirmed = confirmed;
        this.userId = userId;
        this.user = user;
        this.accessToken = accessToken;
    }
}
