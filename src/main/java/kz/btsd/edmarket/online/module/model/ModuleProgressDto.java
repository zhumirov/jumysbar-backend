package kz.btsd.edmarket.online.module.model;

import kz.btsd.edmarket.online.model.SectionProgressDto;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.List;

/**
 * Раздел
 */
@Data
public class ModuleProgressDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private Long position;
    // название раздела
    private String title;

    private double testSize;
    private double resolvedTestSize;
    private int homeworkSize;
    private int resolvedHomeworkSize;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    private List<SectionProgressDto> sections;

    public ModuleProgressDto() {
    }
}
