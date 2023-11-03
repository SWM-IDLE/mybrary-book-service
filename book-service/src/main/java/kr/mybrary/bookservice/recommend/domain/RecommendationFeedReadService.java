package kr.mybrary.bookservice.recommend.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.mybrary.bookservice.client.user.api.UserServiceClient;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse.UserInfo;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedGetWithPagingServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedOfBookGetServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedOfUserGetServiceRequest;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfBookViewModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfUserViewModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;
import kr.mybrary.bookservice.recommend.persistence.repository.RecommendationFeedRepository;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedOfBookViewResponse;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedOfUserViewResponse;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedViewAllResponse;
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

        UserInfoServiceResponse usersInfo = userServiceClient.getUsersInfo(getUserIdFromRecommendationFeedViewAllModel(recommendationFeeds));
        Map<String, UserInfo> userInfoMap = createUserInfoMapFromResponse(usersInfo.getData().getUserInfoElements());

        return RecommendationFeedViewAllResponse.of(recommendationFeeds, userInfoMap, lastRecommendationFeedId);
    }

    public RecommendationFeedOfUserViewResponse findRecommendationFeedOfUserViewResponse(RecommendationFeedOfUserGetServiceRequest request) {

        List<RecommendationFeedOfUserViewModel> recommendationFeeds = recommendationFeedRepository.getRecommendationFeedViewOfUserModel(request.getUserId());
        return RecommendationFeedOfUserViewResponse.of(recommendationFeeds);
    }

    public RecommendationFeedOfBookViewResponse findRecommendationFeedOfBookViewResponse(RecommendationFeedOfBookGetServiceRequest request) {

        List<RecommendationFeedOfBookViewModel> recommendationFeeds = recommendationFeedRepository.getRecommendationFeedViewOfBookModel(request.getBookId());

        UserInfoServiceResponse usersInfo = userServiceClient.getUsersInfo(getUserIdFromRecommendationFeedViewOfBookModel(recommendationFeeds));
        Map<String, UserInfo> userInfoMap = createUserInfoMapFromResponse(usersInfo.getData().getUserInfoElements());

        return RecommendationFeedOfBookViewResponse.of(recommendationFeeds, userInfoMap);
    }

    private Map<String, UserInfo> createUserInfoMapFromResponse(
            List<UserInfo> userInfoServiceResponses) {

        return userInfoServiceResponses.stream()
                .collect(Collectors.toConcurrentMap(
                        UserInfo::getUserId,
                        userInfoServiceResponse -> userInfoServiceResponse)
                );
    }

    private static List<String> getUserIdFromRecommendationFeedViewOfBookModel(List<RecommendationFeedOfBookViewModel> recommendationFeeds) {
        return recommendationFeeds.stream()
                .map(RecommendationFeedOfBookViewModel::getUserId)
                .toList();
    }

    private List<String> getUserIdFromRecommendationFeedViewAllModel(List<RecommendationFeedViewAllModel> recommendationFeeds) {
        return recommendationFeeds.stream()
                .map(RecommendationFeedViewAllModel::getUserId)
                .toList();
    }

}
