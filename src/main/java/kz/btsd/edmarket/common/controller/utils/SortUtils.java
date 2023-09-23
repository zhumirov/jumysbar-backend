package kz.btsd.edmarket.common.controller.utils;

import org.springframework.data.domain.Sort;

public class SortUtils {
    public static Sort buildSort(String sort, String order) {
        Sort sortEntity;
        if ("asc".equals(order)) {
            sortEntity = Sort.by(sort).ascending();
        } else {
            sortEntity = Sort.by(sort).descending();
        }
        return sortEntity;
    }
}
