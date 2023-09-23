package kz.btsd.edmarket.comment.model;

import lombok.Data;

import java.util.List;

@Data
public class SearchResultDto<T> {
    private long totalHits;
    private long homeworkTotalHits;
    private List<T> hits;

    public SearchResultDto(List<T> hits, long totalHits) {
        this.hits = hits;
        this.totalHits = totalHits;
    }

    public SearchResultDto(List<T> hits, long totalHits, long homeworkTotalHits) {
        this.hits = hits;
        this.totalHits = totalHits;
        this.homeworkTotalHits = homeworkTotalHits;
    }
}
