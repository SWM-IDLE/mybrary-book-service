package kr.mybrary.bookservice.recommend.presentation.dto.response;

import java.util.List;
import java.util.Map;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse.UserInfo;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfBookViewModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedOfBookViewResponse {

    private List<RecommendationFeedElement> recommendationFeeds;

    @Getter
    @Builder
    public static class RecommendationFeedElement {

        private String content;
        private List<String> recommendationTargetNames;

        private String userId;
        private String nickname;
        private String profileImageUrl;

    }

    public static RecommendationFeedOfBookViewResponse of(
            List<RecommendationFeedOfBookViewModel> recommendationFeedOfBookViewModels,
            Map<String, UserInfo> userInfoMap) {

        return RecommendationFeedOfBookViewResponse.builder().recommendationFeeds(recommendationFeedOfBookViewModels.stream()
                .filter(recommendationFeedOfBookViewModel -> userInfoMap.containsKey(recommendationFeedOfBookViewModel.getUserId()))
                .map(model -> RecommendationFeedElement.builder()
                        .content(model.getContent())
                        .recommendationTargetNames(model.getRecommendationTargets().stream()
                                .map(RecommendationFeedOfBookViewModel.RecommendationTargetOfBookModel::getTargetName)
                                .toList())
                        .userId(model.getUserId())
                        .nickname(userInfoMap.get(model.getUserId()).getNickname())
                        .profileImageUrl(userInfoMap.get(model.getUserId()).getProfileImageUrl())
                        .build())
                .toList()).build();
    }
}
