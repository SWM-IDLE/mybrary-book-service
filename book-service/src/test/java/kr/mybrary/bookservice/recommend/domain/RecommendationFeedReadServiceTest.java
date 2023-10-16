package kr.mybrary.bookservice.recommend.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import kr.mybrary.bookservice.client.user.api.UserServiceClient;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import kr.mybrary.bookservice.recommend.RecommendationFeedDtoTestData;
import kr.mybrary.bookservice.recommend.persistence.repository.RecommendationFeedRepository;
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

        given(recommendationFeedRepository.getRecommendationFeedViewAll()).willReturn(RecommendationFeedDtoTestData.createRecommendationFeedViewAllModelList());
        given(userServiceClient.getUsersInfo(any())).willReturn(userInfoServiceResponse);

        // when
        RecommendationFeedViewAllResponse response = recommendationFeedReadService.findAll();

        // then
        assertAll(
                () -> assertThat(response.getRecommendationFeeds()).hasSize(5),
                () -> assertThat(response.getRecommendationFeeds()).extracting("userId", "nickname", "profileImageUrl", "content", "myBookId")
                        .contains(tuple("USER_ID_1", "USER_NICKNAME_1", "USER_PICTURE_URL_1", "CONTENT_1", 1L),
                                tuple("USER_ID_2", "USER_NICKNAME_2", "USER_PICTURE_URL_2", "CONTENT_2", 2L),
                                tuple("USER_ID_3", "USER_NICKNAME_3", "USER_PICTURE_URL_3", "CONTENT_3", 3L),
                                tuple("USER_ID_4", "USER_NICKNAME_4", "USER_PICTURE_URL_4", "CONTENT_4", 4L),
                                tuple("USER_ID_5", "USER_NICKNAME_5", "USER_PICTURE_URL_5", "CONTENT_5", 5L))
        );
    }


}