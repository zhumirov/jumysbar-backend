package kz.btsd.edmarket.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventTitleDto {
    private Long id;
    //Название ивента
    private String title;

    public EventTitleDto() {
    }
}
