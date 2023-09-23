package kz.btsd.edmarket.user.model;

import lombok.Data;

@Data
public class UserTokenDto {
    private String accessToken;
    private UserDto user;
    private String param;

    public UserTokenDto() {
    }

    public UserTokenDto(String accessToken, UserDto user, String param) {
        this.accessToken = accessToken;
        this.user = user;
        this.param = param;
    }
}
