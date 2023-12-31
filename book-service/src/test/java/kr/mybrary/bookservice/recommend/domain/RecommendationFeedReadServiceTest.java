package kr.mybrary.bookservice.recommend.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import kr.mybrary.bookservice.client.user.api.UserServiceClient;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import kr.mybrary.bookservice.recommend.RecommendationFeedDtoTestData;
import kr.mybrary.bookservice.recommend.RecommendationFeedFixture;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedGetWithPagingServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedOfBookGetServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedOfMyBookServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedOfUserGetServiceRequest;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import kr.mybrary.bookservice.recommend.persistence.repository.RecommendationFeedRepository;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedOfBookViewResponse;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedOfMyBookResponse;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedOfUserViewResponse;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedViewAllResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RecommendationFeedReadServiceTest {

    @InjectMocks
    private RecommendationFeedReadService recommendationFeedReadService;

    @Mock
    private RecommendationFeedRepository recommendationFeedRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @DisplayName("모든 추천 피드를 조회한다.")
    @Test
    void findAllRecommendationFeeds() {

        // given
        UserInfoServiceResponse userInfoServiceResponse = RecommendationFeedDtoTestData.createUserInfoResponseList();
        RecommendationFeedGetWithPagingServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedGetWithPagingServiceRequest();

        given(recommendationFeedRepository.getRecommendationFeedViewAll(any(), anyInt(), anyString())).willReturn(
                RecommendationFeedDtoTestData.createRecommendationFeedViewAllModelList());
        given(userServiceClient.getUsersInfo(any())).willReturn(userInfoServiceResponse);

        // when
        RecommendationFeedViewAllResponse response = recommendationFeedReadService.findRecommendationFeedWithNoOffsetPaging(
                request);

        // then
        assertAll(
                () -> assertThat(response.getRecommendationFeeds()).hasSize(10),
                () -> assertThat(response.getLastRecommendationFeedId()).isEqualTo(10L),
                () -> assertThat(response.getRecommendationFeeds()).extracting("userId", "nickname", "profileImageUrl", "content", "myBookId")
                        .contains(tuple("USER_ID_1", "USER_NICKNAME_1", "USER_PICTURE_URL_1", "CONTENT_1", 1L),
                                tuple("USER_ID_2", "USER_NICKNAME_2", "USER_PICTURE_URL_2", "CONTENT_2", 2L),
                                tuple("USER_ID_3", "USER_NICKNAME_3", "USER_PICTURE_URL_3", "CONTENT_3", 3L),
                                tuple("USER_ID_4", "USER_NICKNAME_4", "USER_PICTURE_URL_4", "CONTENT_4", 4L),
                                tuple("USER_ID_5", "USER_NICKNAME_5", "USER_PICTURE_URL_5", "CONTENT_5", 5L),
                                tuple("USER_ID_6", "USER_NICKNAME_6", "USER_PICTURE_URL_6", "CONTENT_6", 6L),
                                tuple("USER_ID_7", "USER_NICKNAME_7", "USER_PICTURE_URL_7", "CONTENT_7", 7L),
                                tuple("USER_ID_8", "USER_NICKNAME_8", "USER_PICTURE_URL_8", "CONTENT_8", 8L),
                                tuple("USER_ID_9", "USER_NICKNAME_9", "USER_PICTURE_URL_9", "CONTENT_9", 9L),
                                tuple("USER_ID_10", "USER_NICKNAME_10", "USER_PICTURE_URL_10", "CONTENT_10", 10L)),
                () -> verify(recommendationFeedRepository).getRecommendationFeedViewAll(any(), anyInt(), anyString()),
                () -> verify(userServiceClient).getUsersInfo(any())
        );
    }

    @DisplayName("사용자의 추천 피드를 조회한다.")
    @Test
    void findRecommendationFeedOfUserViewResponse() {

        // given
        RecommendationFeedOfUserGetServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedOfUserGetServiceRequest();

        given(recommendationFeedRepository.getRecommendationFeedViewOfUserModel(anyString())).willReturn(
                RecommendationFeedDtoTestData.createRecommendationFeedOfUserViewModelList());

        // when
        RecommendationFeedOfUserViewResponse response = recommendationFeedReadService.findRecommendationFeedOfUserViewResponse(
                request);

        // then
        assertAll(
                () -> assertThat(response.getRecommendationFeeds()).hasSize(10),
                () -> verify(recommendationFeedRepository, times(1)).getRecommendationFeedViewOfUserModel(anyString())
        );
    }

    @DisplayName("도서의 추천 피드를 조회한다.")
    @Test
    void findRecommendationFeedOfBookViewResponse() {

        // given
        UserInfoServiceResponse userInfoServiceResponse = RecommendationFeedDtoTestData.createUserInfoResponseList();
        RecommendationFeedOfBookGetServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedOfBookGetServiceRequest();

        given(recommendationFeedRepository.getRecommendationFeedViewOfBookModel(any())).willReturn(
                RecommendationFeedDtoTestData.createRecommendationFeedOfBookViewModelList());
        given(userServiceClient.getUsersInfo(any())).willReturn(userInfoServiceResponse);

        // when
        RecommendationFeedOfBookViewResponse response = recommendationFeedReadService.findRecommendationFeedOfBookViewResponse(request);

        // then
        assertAll(
                () -> assertThat(response.getRecommendationFeeds()).hasSize(5),
                () -> assertThat(response.getRecommendationFeeds()).extracting("userId", "nickname", "profileImageUrl", "content")
                        .contains(tuple("USER_ID_1", "USER_NICKNAME_1", "USER_PICTURE_URL_1", "CONTENT_1"),
                                tuple("USER_ID_2", "USER_NICKNAME_2", "USER_PICTURE_URL_2", "CONTENT_2"),
                                tuple("USER_ID_3", "USER_NICKNAME_3", "USER_PICTURE_URL_3", "CONTENT_3"),
                                tuple("USER_ID_4", "USER_NICKNAME_4", "USER_PICTURE_URL_4", "CONTENT_4"),
                                tuple("USER_ID_5", "USER_NICKNAME_5", "USER_PICTURE_URL_5", "CONTENT_5")),
                () -> verify(recommendationFeedRepository).getRecommendationFeedViewOfBookModel(any()),
                () -> verify(userServiceClient).getUsersInfo(any())
        );
    }

    @DisplayName("마이북의 추천 피드를 조회한다.")
    @Test
    void findRecommendationFeedOfMyBookResponse() {

        // given
        RecommendationFeedOfMyBookServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedOfMyBookServiceRequest();
        RecommendationFeed recommendationFeed = RecommendationFeedFixture.COMMON_LOGIN_USER_RECOMMENDATION_FEED.getRecommendationFeed();

        given(recommendationFeedRepository.getRecommendationFeedWithTargetsByMyBookId(any())).willReturn(
                Optional.ofNullable(recommendationFeed));

        // when
        RecommendationFeedOfMyBookResponse response = recommendationFeedReadService.findRecommendationFeedOfMyBookResponse(
                request);

        // then
        assertAll(
                () -> assertThat(recommendationFeed).isNotNull(),
                () -> assertThat(response.getContent()).isEqualTo(recommendationFeed.getContent()),
                () -> verify(recommendationFeedRepository).getRecommendationFeedWithTargetsByMyBookId(any())
        );
    }
}