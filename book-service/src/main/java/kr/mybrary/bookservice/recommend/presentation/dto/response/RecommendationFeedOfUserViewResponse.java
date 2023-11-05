package kr.mybrary.bookservice.recommend.presentation.dto.response;

import java.util.List;
import kr.mybrary.bookservice.global.util.DateUtils;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfUserViewModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfUserViewModel.RecommendationTargetOfUserModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedOfUserViewResponse {

    private List<RecommendationFeedElement> recommendationFeeds;

    public static RecommendationFeedOfUserViewResponse of(List<RecommendationFeedOfUserViewModel> recommendationFeeds) {
        return RecommendationFeedOfUserViewResponse.builder()
                .recommendationFeeds(recommendationFeeds.stream()
                        .map(recommendationFeed -> RecommendationFeedElement.builder()
                                .content(recommendationFeed.getContent())
                                .recommendationFeedId(recommendationFeed.getRecommendationFeedId())
                                .myBookId(recommendationFeed.getMyBookId())
                                .bookId(recommendationFeed.getBookId())
                                .title(recommendationFeed.getTitle())
                                .thumbnailUrl(recommendationFeed.getThumbnailUrl())
                                .isbn13(recommendationFeed.getIsbn13())
                                .createdAt(DateUtils.toDotFormatYYYYMMDD(recommendationFeed.getCreatedAt()))
                                .recommendationTargetNames(recommendationFeed.getRecommendationTargets().stream()
                                        .map(RecommendationTargetOfUserModel::getTargetName).toList())
                                .build()).toList()).build();
    }

    @Getter
    @Builder
    public static class RecommendationFeedElement {

        private String content;
        private Long recommendationFeedId;
        private Long myBookId;
        private Long bookId;
        private String title;
        private String thumbnailUrl;
        private String isbn13;
        private String createdAt;

        private List<String> recommendationTargetNames;
    }
}
