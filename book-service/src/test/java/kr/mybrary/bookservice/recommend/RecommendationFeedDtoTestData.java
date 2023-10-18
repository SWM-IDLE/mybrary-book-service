package kr.mybrary.bookservice.recommend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse.UserInfo;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest.RecommendationFeedCreateServiceRequestBuilder;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedGetWithPagingServiceRequest;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedCreateRequest;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedViewAllResponse;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedViewAllResponse.RecommendationFeedElement;

public class RecommendationFeedDtoTestData {

    public static RecommendationFeedCreateServiceRequestBuilder createRecommendationFeedCreateServiceRequestBuilder() {
        return RecommendationFeedCreateServiceRequest.builder()
                .userId("LOGIN_USER_ID")
                .myBookId(1L)
                .content("NEW CONTENT")
                .recommendationTargetNames(List.of("Target_1", "Target_2"));
    }

    public static RecommendationFeedCreateRequest createRecommendationFeedCreateRequest() {
        return RecommendationFeedCreateRequest.builder()
                .myBookId(1L)
                .content("NEW CONTENT")
                .recommendationTargetNames(List.of("Target_1", "Target_2"))
                .build();
    }

    public static RecommendationFeedGetWithPagingServiceRequest createRecommendationFeedGetWithPagingServiceRequest() {
        return RecommendationFeedGetWithPagingServiceRequest.builder()
                .recommendationFeedId(1L)
                .pageSize(10)
                .build();
    }

    public static UserInfoServiceResponse createUserInfoResponseList() {

        List<UserInfo> list = IntStream.range(1, 11)
                .mapToObj(i -> UserInfo.builder()
                        .userId("USER_ID_" + i)
                        .nickname("USER_NICKNAME_" + i)
                        .profileImageUrl("USER_PICTURE_URL_" + i)
                        .build())
                .toList();

        return UserInfoServiceResponse.builder()
                .data(UserInfoServiceResponse.UserInfoList.builder()
                        .userInfoElements(list)
                        .build())
                .build();
    }

    public static List<RecommendationFeedViewAllModel> createRecommendationFeedViewAllModelList() {

            return IntStream.range(1, 11)
                    .mapToObj(i -> RecommendationFeedViewAllModel.builder()
                            .recommendationFeedId((long) i)
                            .content("CONTENT_" + i)
                            .userId("USER_ID_" + i)
                            .myBookId((long) i)
                            .bookId((long) i)
                            .title("TITLE_" + i)
                            .thumbnailUrl("THUMBNAIL_URL_" + i)
                            .isbn13("ISBN13_" + i)
                            .holderCount(i)
                            .interestCount(i)
                            .recommendationTargets(List.of(
                                    RecommendationFeedViewAllModel.RecommendationTargetModel.builder()
                                            .targetId((long) i)
                                            .targetName("TARGET_NAME_" + i)
                                            .build(),
                                    RecommendationFeedViewAllModel.RecommendationTargetModel.builder()
                                            .targetId((long) i + 1)
                                            .targetName("TARGET_NAME_" + (i + 1)).build()))
                            .bookAuthors(List.of(
                                    RecommendationFeedViewAllModel.BookAuthorModel.builder()
                                                    .authorId((long) i)
                                                    .aid(i)
                                                    .name("AUTHOR_NAME_" + i)
                                                    .build(),
                                    RecommendationFeedViewAllModel.BookAuthorModel.builder()
                                                    .authorId((long) i + 1)
                                                    .aid(i + 1)
                                                    .name("AUTHOR_NAME_" + (i + 1))
                                                    .build()))
                            .build())
                    .toList();
    }

    public static RecommendationFeedViewAllResponse createRecommendationFeedViewAllResponse() {

        List<RecommendationFeedElement> recommendationFeedElements = new ArrayList<>();

        IntStream.range(1, 11)
                .forEach(i -> recommendationFeedElements.add(RecommendationFeedElement.builder()
                        .content("CONTENT_" + i)
                        .recommendationTargetNames(List.of("TARGET_NAME_" + i, "TARGET_NAME_" + (i + 1)))
                        .userId("USER_ID_" + i)
                        .nickname("USER_NICKNAME_" + i)
                        .profileImageUrl("USER_PICTURE_URL_" + i)
                        .myBookId((long) i)
                        .bookId((long) i)
                        .title("TITLE_" + i)
                        .thumbnailUrl("THUMBNAIL_URL_" + i)
                        .isbn13("ISBN13_" + i)
                        .authors(List.of("AUTHOR_NAME_" + i, "AUTHOR_NAME_" + (i + 1)))
                        .holderCount(i)
                        .interestCount(i)
                        .build()));

        return RecommendationFeedViewAllResponse.builder()
                .recommendationFeeds(recommendationFeedElements)
                .lastRecommendationFeedId(10L)
                .build();
    }
}
