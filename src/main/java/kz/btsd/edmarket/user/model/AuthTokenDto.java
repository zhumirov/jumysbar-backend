package kz.btsd.edmarket.user.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthTokenDto {
    private String accessToken;
    private UserDto user;
}
