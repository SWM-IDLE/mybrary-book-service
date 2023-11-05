package kr.mybrary.bookservice.recommend.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedOfBookGetServiceRequest {

    private String isbn13;

    public static RecommendationFeedOfBookGetServiceRequest of(String isbn13) {
        return RecommendationFeedOfBookGetServiceRequest.builder()
                .isbn13(isbn13)
                .build();
    }
}
