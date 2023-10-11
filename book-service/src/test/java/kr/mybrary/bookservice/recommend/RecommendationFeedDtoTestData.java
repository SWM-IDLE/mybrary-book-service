package kr.mybrary.bookservice.recommend;

import java.util.List;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest.RecommendationFeedCreateServiceRequestBuilder;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedCreateRequest;

public class RecommendationFeedDtoTestData {

    public static RecommendationFeedCreateServiceRequestBuilder createRecommendationFeedCreateServiceRequestBuilder() {
        return RecommendationFeedCreateServiceRequest.builder()
                .userId("LOGIN_USER_ID")
                .myBookId(1L)
                .content("NEW CONTENT")
                .recommendationTargetNames(List.of("Target_1", "Target_2"));
    }

    public static RecommendationFeedCreateRequest createRecommendationFeedCreateRequest() {
        return RecommendationFeedCreateRequest.builder()
                .myBookId(1L)
                .content("NEW CONTENT")
                .recommendationTargetNames(List.of("Target_1", "Target_2"))
                .build();
    }
}
