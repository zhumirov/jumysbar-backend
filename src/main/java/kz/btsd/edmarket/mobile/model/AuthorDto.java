package kz.btsd.edmarket.mobile.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public class AuthorDto {
    private String name;
    private String about;
    private String videoId;
    private String avatarId;
}
