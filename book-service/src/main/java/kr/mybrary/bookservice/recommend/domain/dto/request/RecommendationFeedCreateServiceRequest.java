package kr.mybrary.bookservice.recommend.domain.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedCreateServiceRequest {

    private String userId;
    private Long myBookId;
    private String content;
    private List<String> recommendationTargetNames;

}
