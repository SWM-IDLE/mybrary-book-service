package kr.mybrary.bookservice.recommend.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedGetWithPagingServiceRequest {

    private Long recommendationFeedId;
    private int pageSize;
    private String loginId;

    public static RecommendationFeedGetWithPagingServiceRequest of(Long recommendationFeedId, int pageSize, String loginId) {

        return RecommendationFeedGetWithPagingServiceRequest.builder()
                .loginId(loginId)
                .recommendationFeedId(recommendationFeedId)
                .pageSize(pageSize)
                .build();
    }
}
