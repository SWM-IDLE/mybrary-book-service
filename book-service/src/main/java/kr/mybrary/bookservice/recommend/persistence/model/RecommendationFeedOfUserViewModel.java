package kr.mybrary.bookservice.recommend.persistence.model;

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
public class RecommendationFeedOfUserViewModel {

    private String content;
    private List<RecommendationTargetOfUserModel> recommendationTargets;

    private Long recommendationFeedId;
    private Long myBookId;
    private Long bookId;
    private String title;
    private String thumbnailUrl;
    private String isbn13;
    private LocalDateTime createdAt;

    public void setRecommendationTargets(List<RecommendationTargetOfUserModel> recommendationTargetOfUserModels) {
        this.recommendationTargets = recommendationTargetOfUserModels;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RecommendationTargetOfUserModel {

        private Long targetId;
        private String targetName;
    }
}
