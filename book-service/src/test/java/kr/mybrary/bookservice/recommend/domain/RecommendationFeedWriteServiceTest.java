package kr.mybrary.bookservice.recommend.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;

import java.util.List;
import java.util.Optional;
import kr.mybrary.bookservice.mybook.MyBookFixture;
import kr.mybrary.bookservice.mybook.domain.MyBookReadService;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.recommend.RecommendationFeedDtoTestData;
import kr.mybrary.bookservice.recommend.RecommendationFeedFixture;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedDeleteServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedUpdateServiceRequest;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationFeedAccessDeniedException;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationFeedAlreadyExistException;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationTargetDuplicateException;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationTargetLengthExceededException;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationTargetSizeExceededException;
import kr.mybrary.bookservice.recommend.domain.exception.RecommendationTargetSizeLackException;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import kr.mybrary.bookservice.recommend.persistence.repository.RecommendationFeedRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RecommendationFeedWriteServiceTest {

    @InjectMocks
    private RecommendationFeedWriteService recommendationFeedWriteService;

    @Mock
    private RecommendationFeedRepository recommendationFeedRepository;

    @Mock
    private MyBookReadService myBookReadService;

    @DisplayName("추천 피드를 생성한다.")
    @Test
    void createRecommendationFeed() {

        // given
        RecommendationFeedCreateServiceRequest request =
                RecommendationFeedDtoTestData.createRecommendationFeedCreateServiceRequestBuilder().build();
        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook();
        given(myBookReadService.findMyBookById(any())).willReturn(myBook);
        given(recommendationFeedRepository.save(any())).willReturn(null);

        // when
        recommendationFeedWriteService.create(request);

        // then
        assertAll(
                () -> verify(myBookReadService, times(1)).findMyBookById(any()),
                () -> verify(recommendationFeedRepository, times(1)).save(any())
        );
    }

    @DisplayName("추천 피드의 타겟이 5개를 넘으면, 예외가 발생한다.")
    @Test
    void occurExceptionWhenRecommendationTargetGreaterThan5() {

        // given
        RecommendationFeedCreateServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedCreateServiceRequestBuilder()
                .recommendationTargetNames(List.of("Target_1", "Target_2", "Target_3", "Target_4", "Target_5", "Target_6"))
                .build();

        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook();
        given(myBookReadService.findMyBookById(any())).willReturn(myBook);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> recommendationFeedWriteService.create(request))
                        .isInstanceOf(RecommendationTargetSizeExceededException.class),
                () -> verify(myBookReadService, times(1)).findMyBookById(any())
        );
    }

    @DisplayName("추천 피드의 타겟이 1개 미만이라면, 예외가 발생한다.")
    @Test
    void occurExceptionWhenRecommendationTargetLessThan1() {

        // given
        RecommendationFeedCreateServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedCreateServiceRequestBuilder()
                .recommendationTargetNames(List.of())
                .build();

        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook();
        given(myBookReadService.findMyBookById(any())).willReturn(myBook);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> recommendationFeedWriteService.create(request))
                        .isInstanceOf(RecommendationTargetSizeLackException.class),
                () -> verify(myBookReadService, times(1)).findMyBookById(any())
        );
    }

    @DisplayName("추천 피드의 타겟에 중복이 존재하면, 예외가 발생한다.")
    @Test
    void occurExceptionWhenRecommendationTargetDuplicate() {

        // given
        RecommendationFeedCreateServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedCreateServiceRequestBuilder()
                .recommendationTargetNames(List.of("Target_1", "Target_1", "Target_3", "Target_4"))
                .build();

        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook();
        given(myBookReadService.findMyBookById(any())).willReturn(myBook);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> recommendationFeedWriteService.create(request))
                        .isInstanceOf(RecommendationTargetDuplicateException.class),
                () -> verify(myBookReadService, times(1)).findMyBookById(any())
        );
    }

    @DisplayName("한 마이북에 대해서 이미 추천 피드가 존재할 때, 추천 피드를 생성하면 예외가 발생한다")
    @Test
    void occurExceptionWhenRecommendationFeedAlreadyExist() {

        // given
        RecommendationFeedCreateServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedCreateServiceRequestBuilder()
                .myBookId(1L)
                .recommendationTargetNames(List.of("Target_1", "Target_2", "Target_3", "Target_4"))
                .build();

        given(recommendationFeedRepository.existsByMyBookId(1L)).willReturn(true);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> recommendationFeedWriteService.create(request))
                        .isInstanceOf(RecommendationFeedAlreadyExistException.class),
                () -> verify(myBookReadService, never()).findMyBookById(any()),
                () -> verify(recommendationFeedRepository, never()).save(any())
        );
    }

    @DisplayName("추천 피드 대상의 길이가 15자를 넘으면, 예외가 발생한다.")
    @Test
    void occurExceptionWhenRecommendationFeedTargetLengthExceeded() {

        // given
        String targetNameGreaterThan15 = "Target".repeat(4);
        RecommendationFeedCreateServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedCreateServiceRequestBuilder()
                .recommendationTargetNames(List.of(targetNameGreaterThan15, "Target_1", "Target_3", "Target_4"))
                .build();

        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook();
        given(myBookReadService.findMyBookById(any())).willReturn(myBook);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> recommendationFeedWriteService.create(request))
                        .isInstanceOf(RecommendationTargetLengthExceededException.class),
                () -> verify(myBookReadService, times(1)).findMyBookById(any())
        );
    }

    @DisplayName("추천 피드를 삭제한다.")
    @Test
    void deleteRecommendationFeed() {

        // given
        RecommendationFeedDeleteServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedDeleteServiceRequest();
        RecommendationFeed recommendationFeed = RecommendationFeedFixture.COMMON_LOGIN_USER_RECOMMENDATION_FEED.getRecommendationFeed();

        given(recommendationFeedRepository.findById(any())).willReturn(Optional.of(recommendationFeed));
        doNothing().when(recommendationFeedRepository).delete(any());

        // when
        recommendationFeedWriteService.deleteRecommendationFeed(request);

        // then
        assertAll(
                () -> verify(recommendationFeedRepository, times(1)).findById(any()),
                () -> verify(recommendationFeedRepository, times(1)).delete(any())
        );
    }

    @DisplayName("다른 유저의 추천 피드을 삭제시, 예외가 발생한다.")
    @Test
    void occurExceptionWhenDeleteOtherUserRecommendationFeed() {

        // given
        RecommendationFeedDeleteServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedDeleteServiceRequest();
        RecommendationFeed recommendationFeed = RecommendationFeedFixture.COMMON_OTHER_USER_RECOMMENDATION_FEED.getRecommendationFeed();

        given(recommendationFeedRepository.findById(any())).willReturn(Optional.of(recommendationFeed));

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> recommendationFeedWriteService.deleteRecommendationFeed(request))
                        .isInstanceOf(RecommendationFeedAccessDeniedException.class),
                () -> verify(recommendationFeedRepository, times(1)).findById(any()),
                () -> verify(recommendationFeedRepository, never()).delete(any())
        );
    }

    @DisplayName("추천 피드를 수정한다.")
    @Test
    void updateRecommendationFeed() {

        // given
        RecommendationFeedUpdateServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedUpdateServiceRequest();
        RecommendationFeed recommendationFeed = RecommendationFeedFixture.COMMON_LOGIN_USER_RECOMMENDATION_FEED.getRecommendationFeed();
        given(recommendationFeedRepository.findById(any())).willReturn(Optional.of(recommendationFeed));

        // when
        recommendationFeedWriteService.updateRecommendationFeed(request);

        // then
        assertAll(
                () -> verify(recommendationFeedRepository, times(1)).findById(any()),
                () -> assertThat(recommendationFeed.getContent()).isEqualTo(request.getContent()),
                () -> assertThat(recommendationFeed.getRecommendationTargets().getFeedRecommendationTargets()).extracting("targetName")
                        .containsExactlyElementsOf(request.getRecommendationTargetNames())
        );
    }
}