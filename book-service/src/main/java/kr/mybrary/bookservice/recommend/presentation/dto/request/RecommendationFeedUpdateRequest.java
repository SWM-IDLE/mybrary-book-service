package kr.mybrary.bookservice.recommend.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedUpdateRequest {

    @Size(min = 5, max = 50, message = "추천글은 5자 이상 50자 이하로 작성해주세요.")
    @NotBlank(message = "추천글은 필수입니다.")
    private String content;

    private List<String> recommendationTargetNames;
}
