package kz.btsd.edmarket.user.model;

import lombok.Data;

@Data
public class UserPasswordStatusDto {
    private boolean exist;

    public UserPasswordStatusDto(boolean exist) {
        this.exist = exist;
    }
}
