package kr.mybrary.bookservice.recommend.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedOfUserGetServiceRequest {

    private String loginId;
    private String userId;

    public static RecommendationFeedOfUserGetServiceRequest of(String userId, String loginId) {
        return RecommendationFeedOfUserGetServiceRequest.builder()
                .userId(userId)
                .loginId(loginId)
                .build();
    }
}
