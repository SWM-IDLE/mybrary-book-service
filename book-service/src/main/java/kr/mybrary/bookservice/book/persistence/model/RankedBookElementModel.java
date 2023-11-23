package kr.mybrary.bookservice.book.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankedBookElementModel {

    private String title;
    private String isbn13;
    private String thumbnailUrl;

    private Integer holderCount;
    private Integer readCount;
    private Integer interestCount;
    private Integer recommendationFeedCount;
    private Double starRating;
    private Integer reviewCount;

}
