package kr.mybrary.bookservice.recommend.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedGetWithPagingServiceRequest {

    private Long recommendationFeedId;
    private int pageSize;

    public static RecommendationFeedGetWithPagingServiceRequest of(Long recommendationFeedId, int pageSize) {

        return RecommendationFeedGetWithPagingServiceRequest.builder()
                .recommendationFeedId(recommendationFeedId)
                .pageSize(pageSize)
                .build();
    }
}
