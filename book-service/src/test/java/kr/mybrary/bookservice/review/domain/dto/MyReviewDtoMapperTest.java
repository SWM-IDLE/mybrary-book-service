package kr.mybrary.bookservice.review.domain.dto;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import kr.mybrary.bookservice.review.MyReviewDtoTestData;
import kr.mybrary.bookservice.review.persistence.model.MyReviewElementByUserIdModel;
import kr.mybrary.bookservice.review.persistence.model.MyReviewFromMyBookModel;
import kr.mybrary.bookservice.review.presentation.dto.response.MyReviewOfMyBookGetResponse;
import kr.mybrary.bookservice.review.presentation.dto.response.MyReviewOfUserIdGetResponse.MyReviewOfUserIdElement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MyReviewDtoMapperTest {

    @DisplayName("마이북 리뷰 모델을 응답 DTO로 변환한다.")
    @Test
    void ReviewOfMyBookModelToResponse() {

        // given
        MyReviewFromMyBookModel source = MyReviewDtoTestData.createReviewFromMyBookModel();

        // when
        MyReviewOfMyBookGetResponse target = MyReviewDtoMapper.INSTANCE.reviewOfMyBookModelToResponse(source);

        // then
        assertAll(
                () -> assertThat(target.getId()).isEqualTo(source.getId()),
                () -> assertThat(target.getContent()).isEqualTo(source.getContent()),
                () -> assertThat(target.getStarRating()).isEqualTo(source.getStarRating()),
                () -> assertThat(target.getCreatedAt()).isEqualTo(MyReviewDtoMapper.toFormatMyBookReviewUI(source.getCreatedAt())),
                () -> assertThat(target.getUpdatedAt()).isEqualTo(MyReviewDtoMapper.toFormatMyBookReviewUI(source.getUpdatedAt()))
        );
    }

    @DisplayName("LocalDateTime을 yyyy.MM.dd에 맞는 형식으로 변환한다.")
    @Test
    void toFormatMyBookReviewUI() {

        // given
        String expected = "2021.01.01";

        // when
        LocalDateTime source = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        String target = MyReviewDtoMapper.toFormatMyBookReviewUI(source);

        // then
        assertThat(target).isEqualTo(expected);
    }

    @DisplayName("MyReviewElementByUserIdModel를 MyReviewOfUserIdElement로 매핑한다.")
    @Test
    void myReviewElementByUserIdModelToMyReviewOfUserIdElement() {

        // given
        MyReviewElementByUserIdModel source = MyReviewDtoTestData.createMyReviewElementByUserIdModel();

        // when
        MyReviewOfUserIdElement target = MyReviewDtoMapper.INSTANCE.myReviewByUserIdModelToElement(
                source);

        // then
        assertAll(
                () -> assertThat(target.getReviewId()).isEqualTo(source.getReviewId()),
                () -> assertThat(target.getMyBookId()).isEqualTo(source.getMyBookId()),
                () -> assertThat(target.getBookTitle()).isEqualTo(source.getBookTitle()),
                () -> assertThat(target.getBookIsbn13()).isEqualTo(source.getBookIsbn13()),
                () -> assertThat(target.getBookThumbnailUrl()).isEqualTo(source.getBookThumbnailUrl()),
                () -> assertThat(target.getContent()).isEqualTo(source.getContent()),
                () -> assertThat(target.getStarRating()).isEqualTo(source.getStarRating()),
                () -> assertThat(target.getCreatedAt()).isEqualTo(MyReviewDtoMapper.toFormatMyBookReviewUI(source.getCreatedAt())),
                () -> assertThat(target.getUpdatedAt()).isEqualTo(MyReviewDtoMapper.toFormatMyBookReviewUI(source.getUpdatedAt())),
                () -> assertThat(target.getAuthors()).hasSize(2),
                () -> assertThat(target.getAuthors()).containsExactlyInAnyOrder("Author_Name_1", "Author_Name_2")
        );
    }
}