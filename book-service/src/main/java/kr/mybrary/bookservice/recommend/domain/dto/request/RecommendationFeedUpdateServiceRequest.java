package kr.mybrary.bookservice.recommend.domain.dto.request;

import java.util.List;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedUpdateRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedUpdateServiceRequest {

    private String loginId;
    private Long recommendationFeedId;

    private String content;
    private List<String> recommendationTargetNames;

    public static RecommendationFeedUpdateServiceRequest of(String loginId, Long recommendationFeedId, RecommendationFeedUpdateRequest request) {
        return RecommendationFeedUpdateServiceRequest.builder()
                .loginId(loginId)
                .recommendationFeedId(recommendationFeedId)
                .content(request.getContent())
                .recommendationTargetNames(request.getRecommendationTargetNames())
                .build();
    }

}
