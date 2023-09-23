package kz.btsd.edmarket.online.subscription;

import lombok.Data;

import java.util.Date;

/**
 * Раздел
 */
@Data
public class OnlineSubscriptionDto {
    private Long id;
    private Long eventId;
    private String fileId;
    private Long userId;
    private String title;
    private String name;
    private String shortDescription;
    //количество шагов
    private Integer subsectionsSize;
    //количество пройденных шагов
    private Integer viewedSubsectionsSize;
    private Date createdDate;
    private Long test;

    public OnlineSubscriptionDto() {
    }

    public OnlineSubscriptionDto(Long id, Long eventId, String fileId, Long userId, String title, String name, String shortDescription, Integer subsectionsSize, Integer viewedSubsectionsSize, Date createdDate) {
        this.id = id;
        this.eventId = eventId;
        this.fileId = fileId;
        this.userId = userId;
        this.title = title;
        this.name = name;
        this.shortDescription = shortDescription;
        this.subsectionsSize = subsectionsSize;
        this.viewedSubsectionsSize = viewedSubsectionsSize;
        this.createdDate = createdDate;
    }
}
