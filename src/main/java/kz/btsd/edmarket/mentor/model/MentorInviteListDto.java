package kz.btsd.edmarket.mentor.model;

import lombok.Data;

import java.util.List;

@Data
public class MentorInviteListDto {
    private List<String> phones;
    private Long eventId;
}
