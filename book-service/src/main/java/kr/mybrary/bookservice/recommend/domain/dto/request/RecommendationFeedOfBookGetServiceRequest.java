package kr.mybrary.bookservice.recommend.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedOfBookGetServiceRequest {

    private Long bookId;
}
