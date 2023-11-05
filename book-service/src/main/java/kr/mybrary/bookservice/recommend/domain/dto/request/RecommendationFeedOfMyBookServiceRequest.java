package kr.mybrary.bookservice.recommend.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedOfMyBookServiceRequest {

    private Long myBookId;

    public static RecommendationFeedOfMyBookServiceRequest of(Long myBookId) {

        return RecommendationFeedOfMyBookServiceRequest.builder()
                .myBookId(myBookId)
                .build();
    }
}
