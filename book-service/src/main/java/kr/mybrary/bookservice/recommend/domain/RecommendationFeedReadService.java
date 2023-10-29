package kr.mybrary.bookservice.recommend.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.mybrary.bookservice.client.user.api.UserServiceClient;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse.UserInfo;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedGetWithPagingServiceRequest;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;
import kr.mybrary.bookservice.recommend.persistence.repository.RecommendationFeedRepository;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedViewAllResponse;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedViewAllResponse.RecommendationFeedElement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendationFeedReadService {

    private final RecommendationFeedRepository recommendationFeedRepository;
    private final UserServiceClient userServiceClient;

    public RecommendationFeedViewAllResponse findRecommendationFeedWithNoOffsetPaging(RecommendationFeedGetWithPagingServiceRequest request) {

        List<RecommendationFeedViewAllModel> recommendationFeeds =
                recommendationFeedRepository.getRecommendationFeedViewAll(request.getRecommendationFeedId(), request.getPageSize(), request.getLoginId());
        Long lastRecommendationFeedId = recommendationFeeds.get(recommendationFeeds.size() - 1).getRecommendationFeedId();

        UserInfoServiceResponse usersInfo = userServiceClient.getUsersInfo(getUserIdFromRecommendationFeed(recommendationFeeds));
        Map<String, UserInfo> userInfoMap = createUserInfoMapFromResponse(usersInfo.getData().getUserInfoElements());

        List<RecommendationFeedElement> recommendationFeedElements = createRecommendationFeedElements(recommendationFeeds, userInfoMap);
        return RecommendationFeedViewAllResponse.of(recommendationFeedElements, lastRecommendationFeedId);
    }

    private List<RecommendationFeedElement> createRecommendationFeedElements(
            List<RecommendationFeedViewAllModel> recommendationFeeds, Map<String, UserInfo> userInfoMap) {

        return recommendationFeeds.stream()
                .filter(recommendationFeed -> userInfoMap.containsKey(recommendationFeed.getUserId()))
                .map(recommendationFeed -> RecommendationFeedElement.of(recommendationFeed, userInfoMap)).toList();
    }

    private Map<String, UserInfo> createUserInfoMapFromResponse(
            List<UserInfo> userInfoServiceResponses) {

        return userInfoServiceResponses.stream()
                .collect(Collectors.toConcurrentMap(
                        UserInfo::getUserId,
                        userInfoServiceResponse -> userInfoServiceResponse)
                );
    }

    private List<String> getUserIdFromRecommendationFeed(List<RecommendationFeedViewAllModel> recommendationFeeds) {
        return recommendationFeeds.stream()
                .map(RecommendationFeedViewAllModel::getUserId)
                .toList();
    }

}
