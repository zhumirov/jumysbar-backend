package kz.btsd.edmarket.event.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CategoriesResponse {
    private List<String> categories;
    private Map<String, List> subCategories;
}
