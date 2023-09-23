package kz.btsd.edmarket.invitation.service;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

@Data
public class DeleteSubscription {
    private String eventTitle;
    private String userName;
    private String email;

    public DeleteSubscription(String eventTitle, String userName, String email) {
        this.eventTitle = eventTitle;
        this.userName = userName;
        this.email = email;
    }
}
