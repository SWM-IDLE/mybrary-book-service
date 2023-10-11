package kr.mybrary.bookservice.recommend.presentation;

import kr.mybrary.bookservice.global.dto.response.SuccessResponse;
import kr.mybrary.bookservice.recommend.domain.RecommendationFeedWriteService;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecommendationFeedController {

    private final RecommendationFeedWriteService recommendationFeedWriteService;

    @PostMapping("/recommendation-feeds")
    public ResponseEntity<SuccessResponse<Void>> createRecommendationFeed(
            @RequestHeader("USER-ID") String loginId,
            @RequestBody RecommendationFeedCreateRequest request) {

        RecommendationFeedCreateServiceRequest serviceRequest = RecommendationFeedCreateServiceRequest.of(loginId, request);
        recommendationFeedWriteService.create(serviceRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(HttpStatus.CREATED.toString(), "추천 피드를 작성하였습니다.", null));
    }
}
