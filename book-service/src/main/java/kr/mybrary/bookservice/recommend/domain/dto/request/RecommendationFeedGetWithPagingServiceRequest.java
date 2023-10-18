package kr.mybrary.bookservice.recommend.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedGetWithPagingServiceRequest {

    private Long recommendationFeedId;
    private int pageSize;
}
