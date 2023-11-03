package kr.mybrary.bookservice.recommend;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse.UserInfo;
import kr.mybrary.bookservice.global.util.DateUtils;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest.RecommendationFeedCreateServiceRequestBuilder;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedDeleteServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedGetWithPagingServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedOfBookGetServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedOfMyBookServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedOfUserGetServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedUpdateServiceRequest;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfBookViewModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfBookViewModel.RecommendationTargetOfBookModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfUserViewModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedCreateRequest;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedUpdateRequest;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedOfBookViewResponse;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedOfMyBookResponse;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedOfUserViewResponse;
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
                .loginId("LOGIN_USER_ID")
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
                            .interested(true)
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
                        .interested(true)
                        .build()));

        return RecommendationFeedViewAllResponse.builder()
                .recommendationFeeds(recommendationFeedElements)
                .lastRecommendationFeedId(10L)
                .build();
    }

    public static RecommendationFeedDeleteServiceRequest createRecommendationFeedDeleteServiceRequest() {

        return RecommendationFeedDeleteServiceRequest.builder()
                .recommendationFeedId(1L)
                .loginId("LOGIN_USER_ID")
                .build();
    }

    public static RecommendationFeedUpdateServiceRequest createRecommendationFeedUpdateServiceRequest() {
        return RecommendationFeedUpdateServiceRequest.builder()
                .recommendationFeedId(1L)
                .loginId("LOGIN_USER_ID")
                .content("NEW CONTENT")
                .recommendationTargetNames(List.of("New Target 1", "New Target 2"))
                .build();
    }

    public static RecommendationFeedUpdateRequest createRecommendationFeedUpdateRequest() {
        return RecommendationFeedUpdateRequest.builder()
                .content("NEW CONTENT")
                .recommendationTargetNames(List.of("New Target 1", "New Target 2"))
                .build();
    }

    public static RecommendationFeedOfUserGetServiceRequest createRecommendationFeedOfUserGetServiceRequest() {
        return RecommendationFeedOfUserGetServiceRequest.builder()
                .userId("LOGIN_USER_ID")
                .build();
    }

    public static List<RecommendationFeedOfUserViewModel> createRecommendationFeedOfUserViewModelList() {

            return IntStream.range(1, 11)
                    .mapToObj(i -> RecommendationFeedOfUserViewModel.builder()
                            .recommendationFeedId((long) i)
                            .content("CONTENT_" + i)
                            .myBookId((long) i)
                            .bookId((long) i)
                            .title("TITLE_" + i)
                            .thumbnailUrl("THUMBNAIL_URL_" + i)
                            .isbn13("ISBN13_" + i)
                            .createdAt(LocalDateTime.now())
                            .recommendationTargets(List.of(
                                    RecommendationFeedOfUserViewModel.RecommendationTargetOfUserModel.builder()
                                            .targetId((long) i)
                                            .targetName("TARGET_NAME_" + i)
                                            .build(),
                                    RecommendationFeedOfUserViewModel.RecommendationTargetOfUserModel.builder()
                                            .targetId((long) i + 1)
                                            .targetName("TARGET_NAME_" + (i + 1)).build()))
                            .build())
                    .toList();
    }

    public static RecommendationFeedOfUserViewResponse createRecommendationFeedOfUserViewResponse() {

            List<RecommendationFeedOfUserViewResponse.RecommendationFeedElement> recommendationFeedElements = new ArrayList<>();

            IntStream.range(1, 5)
                    .forEach(i -> recommendationFeedElements.add(RecommendationFeedOfUserViewResponse.RecommendationFeedElement.builder()
                            .content("CONTENT_" + i)
                            .recommendationTargetNames(List.of("TARGET_NAME_" + i, "TARGET_NAME_" + (i + 1)))
                            .recommendationFeedId((long) i)
                            .myBookId((long) i)
                            .bookId((long) i)
                            .title("TITLE_" + i)
                            .thumbnailUrl("THUMBNAIL_URL_" + i)
                            .isbn13("ISBN13_" + i)
                            .createdAt(DateUtils.toDotFormatYYYYMMDD(LocalDateTime.now()))
                            .build()));

            return RecommendationFeedOfUserViewResponse.builder()
                    .recommendationFeeds(recommendationFeedElements)
                    .build();
    }

    public static RecommendationFeedOfBookGetServiceRequest createRecommendationFeedOfBookGetServiceRequest() {
        return RecommendationFeedOfBookGetServiceRequest.builder()
                .isbn13("ISBN13")
                .build();
    }

    public static List<RecommendationFeedOfBookViewModel> createRecommendationFeedOfBookViewModelList() {

        List<RecommendationFeedOfBookViewModel> recommendationFeedOfBookViewModels = new ArrayList<>();

        IntStream.range(1, 6)
                .forEach(i -> recommendationFeedOfBookViewModels.add(RecommendationFeedOfBookViewModel.builder()
                        .recommendationFeedId((long) i)
                        .content("CONTENT_" + i)
                        .userId("USER_ID_" + i)
                        .createdAt(LocalDateTime.now())
                        .recommendationTargets(List.of(
                                RecommendationTargetOfBookModel.builder()
                                .targetId((long) i)
                                .targetName("TARGET_NAME_" + i)
                                .build(),
                                RecommendationTargetOfBookModel.builder()
                                .targetId((long) i + 1)
                                .targetName("TARGET_NAME_" + (i + 1)).build()))
                        .build()));

        return recommendationFeedOfBookViewModels;
    }

    public static RecommendationFeedOfBookViewResponse createRecommendationFeedOfBookViewResponse() {

        return RecommendationFeedOfBookViewResponse.builder()
                .recommendationFeeds(
                        List.of(
                                RecommendationFeedOfBookViewResponse.RecommendationFeedElement.builder()
                                        .userId("USER_ID_1")
                                        .nickname("USER_NICKNAME_1")
                                        .profileImageUrl("USER_PICTURE_URL_1")
                                        .content("CONTENT_1")
                                        .recommendationTargetNames(List.of("TARGET_NAME_1", "TARGET_NAME_2"))
                                        .build(),
                                RecommendationFeedOfBookViewResponse.RecommendationFeedElement.builder()
                                        .userId("USER_ID_2")
                                        .nickname("USER_NICKNAME_2")
                                        .profileImageUrl("USER_PICTURE_URL_2")
                                        .content("CONTENT_2")
                                        .recommendationTargetNames(List.of("TARGET_NAME_3", "TARGET_NAME_4"))
                                        .build()
                        )
                ).build();
    }

    public static RecommendationFeedOfMyBookServiceRequest createRecommendationFeedOfMyBookServiceRequest() {
        return RecommendationFeedOfMyBookServiceRequest.builder()
                .myBookId(1L)
                .build();
    }

    public static RecommendationFeedOfMyBookResponse createRecommendationFeedOfMyBookResponse() {
        return RecommendationFeedOfMyBookResponse.builder()
                .recommendationFeedId(1L)
                .content("CONTENT")
                .recommendationTargetNames(List.of("TARGET_NAME_1", "TARGET_NAME_2"))
                .build();
    }
}
