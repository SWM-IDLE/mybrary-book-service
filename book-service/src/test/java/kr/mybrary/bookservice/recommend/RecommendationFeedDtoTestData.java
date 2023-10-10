package kr.mybrary.bookservice.recommend;

import java.util.List;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest.RecommendationFeedCreateServiceRequestBuilder;

public class RecommendationFeedDtoTestData {

    public static RecommendationFeedCreateServiceRequestBuilder createRecommendationFeedCreateServiceRequestBuilder() {
        return RecommendationFeedCreateServiceRequest.builder()
                .userId("LOGIN_USER_ID")
                .myBookId(1L)
                .content("NEW CONTENT")
                .recommendationTargetNames(List.of("Target_1", "Target_2"));
    }
}
