package kz.btsd.edmarket.comment.like.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

@Data
public class StepLikeDto {
    private boolean present=false;
    private StepLike stepLike;

    public StepLikeDto() {
    }
}
