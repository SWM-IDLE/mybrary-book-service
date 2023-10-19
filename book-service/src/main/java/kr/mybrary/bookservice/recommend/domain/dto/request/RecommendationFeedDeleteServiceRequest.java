package kr.mybrary.bookservice.recommend.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedDeleteServiceRequest {

    private Long recommendationFeedId;
    private String loginId;

    public static RecommendationFeedDeleteServiceRequest of(Long recommendationFeedId, String loginId) {
        return RecommendationFeedDeleteServiceRequest.builder()
                .recommendationFeedId(recommendationFeedId)
                .loginId(loginId)
                .build();
    }

}
