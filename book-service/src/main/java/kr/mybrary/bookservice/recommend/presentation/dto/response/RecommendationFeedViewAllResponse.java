package kr.mybrary.bookservice.recommend.presentation.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedViewAllResponse {

    private List<RecommendationFeedElement> recommendationFeeds;

    @Getter
    @Builder
    public static class RecommendationFeedElement {

        private String content;
        private List<String> recommendationTargetNames;

        private String userId;
        private String nickname;
        private String profileImageUrl;

        private Long myBookId;
        private Long bookId;
        private String title;
        private String thumbnailUrl;
        private String isbn13;
        private List<String> authors;
        private Integer holderCount;
        private Integer interestCount;
    }

    public static RecommendationFeedViewAllResponse of(List<RecommendationFeedElement> recommendationFeeds) {
        return RecommendationFeedViewAllResponse.builder()
                .recommendationFeeds(recommendationFeeds)
                .build();
    }
}
