package kr.mybrary.bookservice.book.presentation.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
public class BookRankedListByResponse {

    List<BookRankedElement> books;

    @Getter
    @Builder
    @ToString
    public static class BookRankedElement {

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
}
