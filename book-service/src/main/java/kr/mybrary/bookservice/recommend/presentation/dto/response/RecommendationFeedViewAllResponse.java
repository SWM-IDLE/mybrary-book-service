package kr.mybrary.bookservice.recommend.presentation.dto.response;

import java.util.List;
import java.util.Map;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse.UserInfo;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel.BookAuthorModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel.RecommendationTargetModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedViewAllResponse {

    private Long lastRecommendationFeedId;
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
        private Boolean interested;

        public static RecommendationFeedElement of(RecommendationFeedViewAllModel recommendationFeed,
                                                   Map<String, UserInfo> userInfoMap) {

            return RecommendationFeedElement.builder()
                    .content(recommendationFeed.getContent())
                    .recommendationTargetNames(recommendationFeed.getRecommendationTargets().stream()
                            .map(RecommendationTargetModel::getTargetName)
                            .toList())
                    .userId(recommendationFeed.getUserId())
                    .nickname(userInfoMap.get(recommendationFeed.getUserId()).getNickname())
                    .profileImageUrl(userInfoMap.get(recommendationFeed.getUserId()).getProfileImageUrl())
                    .myBookId(recommendationFeed.getMyBookId())
                    .bookId(recommendationFeed.getBookId())
                    .title(recommendationFeed.getTitle())
                    .thumbnailUrl(recommendationFeed.getThumbnailUrl())
                    .isbn13(recommendationFeed.getIsbn13())
                    .authors(recommendationFeed.getBookAuthors().stream()
                            .map(BookAuthorModel::getName)
                            .toList())
                    .holderCount(recommendationFeed.getHolderCount())
                    .interestCount(recommendationFeed.getInterestCount())
                    .interested(recommendationFeed.getInterested())
                    .build();
        }
    }

    public static RecommendationFeedViewAllResponse of(List<RecommendationFeedElement> recommendationFeeds, Long lastRecommendationFeedId) {
        return RecommendationFeedViewAllResponse.builder()
                .recommendationFeeds(recommendationFeeds)
                .lastRecommendationFeedId(lastRecommendationFeedId)
                .build();
    }
}
