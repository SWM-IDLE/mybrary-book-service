package kr.mybrary.bookservice.recommend.presentation.dto.response;

import java.util.List;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import kr.mybrary.bookservice.recommend.persistence.RecommendationTarget;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedOfMyBookResponse {

    private Long recommendationFeedId;
    private String content;
    private List<String> recommendationTargetNames;

    public static RecommendationFeedOfMyBookResponse of(RecommendationFeed recommendationFeed) {
        return RecommendationFeedOfMyBookResponse.builder()
                .recommendationFeedId(recommendationFeed.getId())
                .content(recommendationFeed.getContent())
                .recommendationTargetNames(recommendationFeed.getRecommendationTargets().
                        getFeedRecommendationTargets().stream().map(RecommendationTarget::getTargetName).toList())
                .build();
    }
}
