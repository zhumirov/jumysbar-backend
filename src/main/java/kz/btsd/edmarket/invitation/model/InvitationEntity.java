package kz.btsd.edmarket.invitation.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

@Data
@Entity(name = "invitation")
public class InvitationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invitation_seq")
    @SequenceGenerator(name = "invitation_seq", sequenceName = "invitation_seq",
            allocationSize = 1)
    private Long id;
    private String phone;
    private String email;
    private Long eventId;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public InvitationEntity() {
    }

    public InvitationEntity(String phone, Long eventId) {
        this.phone = phone;
        this.eventId = eventId;
    }

    public InvitationEntity(Long eventId, String email) {
        this.eventId = eventId;
        this.email = email;
    }
}
