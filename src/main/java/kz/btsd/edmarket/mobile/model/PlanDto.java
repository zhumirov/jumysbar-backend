package kz.btsd.edmarket.mobile.model;

import kz.btsd.edmarket.event.model.EntityStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Accessors(chain = true)
@Data
public class PlanDto {
    private Long id;
    private String title;
    private List<String> options;
    private Long price;
    @Enumerated(EnumType.STRING)
    private EntityStatus status = EntityStatus.NEW;
}
