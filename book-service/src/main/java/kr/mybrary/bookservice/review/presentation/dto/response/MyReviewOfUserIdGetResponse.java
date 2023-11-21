package kr.mybrary.bookservice.review.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyReviewOfUserIdGetResponse {

    private List<MyReviewOfUserIdElement> reviews;

    @Getter
    @Builder
    public static class MyReviewOfUserIdElement {

        private Long reviewId;
        private Long myBookId;
        private String bookTitle;
        private String bookIsbn13;
        private String bookThumbnailUrl;

        private String content;
        private Double starRating;
        private String createdAt;
        private String updatedAt;
    }

}
