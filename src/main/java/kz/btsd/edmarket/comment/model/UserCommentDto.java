package kz.btsd.edmarket.comment.model;

import kz.btsd.edmarket.user.model.UserRole;
import lombok.Data;

@Data
public class UserCommentDto {
    private Long id;
    private String name;
    private String fileId;
    private UserRole eventRole;

    public UserCommentDto() {
    }
}
