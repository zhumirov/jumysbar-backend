package kz.btsd.edmarket.user.model;

import lombok.Data;

@Data
public class UserShortDto {
    private Long id;
    private String name;

    public UserShortDto() {
    }

    public UserShortDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
