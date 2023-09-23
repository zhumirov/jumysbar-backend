package kz.btsd.edmarket.event.controller.search;

import kz.btsd.edmarket.user.model.Platform;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

@Data
public class EventSearchBody {
    private Long userId;
    private Integer page = 0;
    private Integer size = 100;
    private List<Platform> platforms = Arrays.asList(Platform.JUMYSBAR);
    private String fieldSort = "createdDate";
    private Sort.Direction order = Sort.Direction.ASC;
}
