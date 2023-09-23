package kz.btsd.edmarket.mentor.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MentorInviteDto {
    @Size(min = 10, max = 15)
    @NotNull
    private String phone;
    @NotNull
    private Long eventId;
}
