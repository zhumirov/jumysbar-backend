package kz.btsd.edmarket.comment.model;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class CommentDeleteDto {
    @Enumerated(EnumType.STRING)
    private CommentStatus status;
    private CommentDto comment;

    public CommentDeleteDto() {
    }

    public CommentDeleteDto(CommentStatus status) {
        this.status = status;
    }

    public CommentDeleteDto(CommentStatus status, CommentDto comment) {
        this.status = status;
        this.comment = comment;
    }
}
