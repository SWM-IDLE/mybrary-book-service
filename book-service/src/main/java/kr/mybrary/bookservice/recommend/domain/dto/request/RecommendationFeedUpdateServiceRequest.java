package kr.mybrary.bookservice.recommend.domain.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedUpdateServiceRequest {

    private String loginId;
    private Long recommendationFeedId;

    private String content;
    private List<String> recommendationTargetNames;

}
