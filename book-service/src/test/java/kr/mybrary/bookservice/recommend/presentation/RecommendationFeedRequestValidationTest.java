package kr.mybrary.bookservice.recommend.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedCreateRequest;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendationFeedRequestValidationTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("추천 피드 생성시, 추천피드 내 글은 5자 이상 50자 이하로 작성해야 한다.")
    @Test
    void validateRecommendationFeedCreateRequest_1() {

        // given
        RecommendationFeedCreateRequest recommendationFeedCreateRequest_1 = RecommendationFeedCreateRequest.builder()
                .content("추천글")
                .build();

        String contentGreaterThan50 = "추천글".repeat(20);
        RecommendationFeedCreateRequest recommendationFeedCreateRequest_2 = RecommendationFeedCreateRequest.builder()
                .content(contentGreaterThan50)
                .build();

        // when
        Set<ConstraintViolation<RecommendationFeedCreateRequest>> constraintViolations_1 = validator.validate(recommendationFeedCreateRequest_1);
        Set<ConstraintViolation<RecommendationFeedCreateRequest>> constraintViolations_2 = validator.validate(recommendationFeedCreateRequest_2);

        // then
        assertAll(
                () -> assertThat(constraintViolations_1).extracting(ConstraintViolation::getMessage)
                        .contains("추천글은 5자 이상 50자 이하로 작성해주세요."),
                () -> assertThat(constraintViolations_2).extracting(ConstraintViolation::getMessage)
                        .contains("추천글은 5자 이상 50자 이하로 작성해주세요.")
        );
    }

    @DisplayName("추천 피드 생성시, 추천피드 내 글은 필수로 작성해야 한다.")
    @Test
    void validateRecommendationFeedCreateRequest_2() {

        // given
        RecommendationFeedCreateRequest recommendationFeedCreateRequest_1 = RecommendationFeedCreateRequest.builder()
                .build();

        // when
        Set<ConstraintViolation<RecommendationFeedCreateRequest>> constraintViolations_1 = validator.validate(recommendationFeedCreateRequest_1);

        // then
        assertAll(
                () -> assertThat(constraintViolations_1).extracting(ConstraintViolation::getMessage)
                        .contains("추천글은 필수입니다.")
        );
    }

    @DisplayName("추천 피드 수정시, 추천피드 내 글은 5자 이상 50자 이하로 작성해야 한다.")
    @Test
    void validateRecommendationFeedUpdateRequest_1() {

        // given
        RecommendationFeedUpdateRequest recommendationFeedUpdateRequest_1 = RecommendationFeedUpdateRequest.builder()
                .content("추천글")
                .build();

        String contentGreaterThan50 = "추천글".repeat(20);
        RecommendationFeedUpdateRequest recommendationFeedUpdateRequest_2 = RecommendationFeedUpdateRequest.builder()
                .content(contentGreaterThan50)
                .build();

        // when
        Set<ConstraintViolation<RecommendationFeedUpdateRequest>> constraintViolations_1 = validator.validate(recommendationFeedUpdateRequest_1);
        Set<ConstraintViolation<RecommendationFeedUpdateRequest>> constraintViolations_2 = validator.validate(recommendationFeedUpdateRequest_2);

        // then
        assertAll(
                () -> assertThat(constraintViolations_1).extracting(ConstraintViolation::getMessage)
                        .contains("추천글은 5자 이상 50자 이하로 작성해주세요."),
                () -> assertThat(constraintViolations_2).extracting(ConstraintViolation::getMessage)
                        .contains("추천글은 5자 이상 50자 이하로 작성해주세요.")
        );
    }

    @DisplayName("추천 피드 수정시, 추천피드 내 글은 필수로 작성해야 한다.")
    @Test
    void validateRecommendationFeedUpdateRequest_2() {

        // given
        RecommendationFeedUpdateRequest recommendationFeedUpdateRequest_1 = RecommendationFeedUpdateRequest.builder()
                .build();

        // when
        Set<ConstraintViolation<RecommendationFeedUpdateRequest>> constraintViolations_1 = validator.validate(recommendationFeedUpdateRequest_1);

        // then
        assertAll(
                () -> assertThat(constraintViolations_1).extracting(ConstraintViolation::getMessage)
                        .contains("추천글은 필수입니다.")
        );
    }
}
