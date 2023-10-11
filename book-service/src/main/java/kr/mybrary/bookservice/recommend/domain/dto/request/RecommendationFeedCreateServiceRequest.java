package kr.mybrary.bookservice.recommend.domain.dto.request;

import java.util.List;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedCreateRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedCreateServiceRequest {

    private String userId;
    private Long myBookId;
    private String content;
    private List<String> recommendationTargetNames;

    public static RecommendationFeedCreateServiceRequest of(String userId, RecommendationFeedCreateRequest request) {
        return RecommendationFeedCreateServiceRequest.builder()
                .userId(userId)
                .myBookId(request.getMyBookId())
                .content(request.getContent())
                .recommendationTargetNames(request.getRecommendationTargetNames())
                .build();
    }
}
