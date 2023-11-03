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
public class RecommendationFeedOfBookViewModel {

    private Long recommendationFeedId;
    private String content;
    private List<RecommendationTargetOfBookModel> recommendationTargets;
    private LocalDateTime createdAt;

    public void setRecommendationTargets(List<RecommendationTargetOfBookModel> recommendationTargetOfBookModels) {
        this.recommendationTargets = recommendationTargetOfBookModels;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RecommendationTargetOfBookModel {

        private Long targetId;
        private String targetName;
    }
}
