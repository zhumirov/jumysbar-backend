package kz.btsd.edmarket.user.model.link;

import lombok.Data;
import lombok.experimental.Accessors;
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
@Entity
public class InviteLink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_link_seq")
    @SequenceGenerator(name = "inv_link_seq", sequenceName = "inv_link_seq",
            allocationSize = 1)
    private Long id;
    private Long eventId;
    private String uuid;
    @Enumerated(EnumType.STRING)
    private InviteStatus status = InviteStatus.NEW;
    @Enumerated(EnumType.STRING)
    private InviteType type = InviteType.ONE;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public InviteLink() {
    }

    public InviteLink(Long eventId, InviteType type, String uuid) {
        this.eventId = eventId;
        this.type = type;
        this.uuid = uuid;
    }
}
