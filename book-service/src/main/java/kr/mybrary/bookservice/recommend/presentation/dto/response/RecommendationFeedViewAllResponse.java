package kr.mybrary.bookservice.recommend.presentation.dto.response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse.UserInfo;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;
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

        private Long recommendationFeedId;
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
    }

    public static RecommendationFeedViewAllResponse of(List<RecommendationFeedViewAllModel> recommendationFeeds,
                                                       Map<String, UserInfo> userInfoMap,
                                                       Long lastRecommendationFeedId) {

        return RecommendationFeedViewAllResponse.builder()
                .recommendationFeeds(recommendationFeeds.stream()
                        .filter(recommendationFeed -> userInfoMap.containsKey(recommendationFeed.getUserId()))
                        .map(recommendationFeed -> RecommendationFeedElement.builder()
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
                                .authors(getAuthorList(recommendationFeed))
                                .holderCount(recommendationFeed.getHolderCount())
                                .interestCount(recommendationFeed.getInterestCount())
                                .interested(recommendationFeed.getInterested())
                                .recommendationFeedId(recommendationFeed.getRecommendationFeedId())
                                .build())
                        .toList())
                .lastRecommendationFeedId(lastRecommendationFeedId)
                .build();
    }

    private static List<String> getAuthorList(RecommendationFeedViewAllModel recommendationFeed) {
        if (recommendationFeed.getBookAuthors() == null) {
            return List.of();
        }

        return Arrays.stream(recommendationFeed.getBookAuthors().split(", "))
                .toList();
    }
}
