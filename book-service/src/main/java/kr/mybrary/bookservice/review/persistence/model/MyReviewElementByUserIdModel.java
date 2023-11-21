package kr.mybrary.bookservice.review.persistence.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyReviewElementByUserIdModel {

    private Long reviewId;
    private Long myBookId;
    private String bookTitle;
    private String bookIsbn13;
    private String bookThumbnailUrl;

    private String content;
    private Double starRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
