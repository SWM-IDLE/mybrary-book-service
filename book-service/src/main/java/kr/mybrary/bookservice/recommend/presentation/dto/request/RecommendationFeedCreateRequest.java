package kr.mybrary.bookservice.recommend.presentation.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationFeedCreateRequest {

    private Long myBookId;
    private String content;
    private List<String> recommendationTargetNames;
}
