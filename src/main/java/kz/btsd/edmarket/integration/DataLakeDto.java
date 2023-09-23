package kz.btsd.edmarket.integration;

import lombok.Data;

import java.util.List;

@Data
public class DataLakeDto<T> {
    private long totalHits;
    private List<T> hits;

    public DataLakeDto(List<T> hits, long totalHits) {
        this.totalHits = totalHits;
        this.hits = hits;
    }
}
