package kr.mybrary.bookservice.review.persistence.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyReviewElementByUserIdModel {

    private Long reviewId;
    private Long myBookId;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn13;
    private String bookThumbnailUrl;
    private List<BookAuthorModel> bookAuthors;

    private String content;
    private Double starRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setBookAuthors(List<BookAuthorModel> bookAuthorModels) {
        this.bookAuthors = bookAuthorModels;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class BookAuthorModel {

        private Long authorId;
        private Integer aid;
        private String name;
    }
}
