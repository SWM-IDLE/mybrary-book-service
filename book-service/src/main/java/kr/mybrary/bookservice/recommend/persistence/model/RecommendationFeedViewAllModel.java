package kr.mybrary.bookservice.recommend.persistence.model;

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
public class RecommendationFeedViewAllModel {

    private String content;
    private List<RecommendationTargetModel> recommendationTargets;
    private List<BookAuthorModel> bookAuthors;

    private String userId;

    private Long recommendationFeedId;
    private Long myBookId;
    private Long bookId;
    private String title;
    private String thumbnailUrl;
    private String isbn13;
    private Integer holderCount;
    private Integer interestCount;

    public void setRecommendationTargets(List<RecommendationTargetModel> recommendationTargetModels) {
        this.recommendationTargets = recommendationTargetModels;
    }

    public void setBookAuthors(List<BookAuthorModel> bookAuthorModels) {
        this.bookAuthors = bookAuthorModels;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RecommendationTargetModel {

        private Long targetId;
        private String targetName;
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
