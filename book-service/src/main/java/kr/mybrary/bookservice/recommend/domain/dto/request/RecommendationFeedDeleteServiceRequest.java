package kr.mybrary.bookservice.recommend.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedDeleteServiceRequest {

    private Long recommendationFeedId;
    private String loginId;

}
