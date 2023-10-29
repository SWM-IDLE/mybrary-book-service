package kr.mybrary.bookservice.recommend.presentation;

import jakarta.validation.Valid;
import kr.mybrary.bookservice.global.dto.response.SuccessResponse;
import kr.mybrary.bookservice.recommend.domain.RecommendationFeedReadService;
import kr.mybrary.bookservice.recommend.domain.RecommendationFeedWriteService;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedCreateServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedDeleteServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedGetWithPagingServiceRequest;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedUpdateServiceRequest;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedCreateRequest;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedUpdateRequest;
import kr.mybrary.bookservice.recommend.presentation.dto.response.RecommendationFeedViewAllResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecommendationFeedController {

    private final RecommendationFeedWriteService recommendationFeedWriteService;
    private final RecommendationFeedReadService recommendationFeedReadService;

    @PostMapping("/recommendation-feeds")
    public ResponseEntity<SuccessResponse<Void>> createRecommendationFeed(
            @RequestHeader("USER-ID") String loginId,
            @Valid @RequestBody RecommendationFeedCreateRequest request) {

        RecommendationFeedCreateServiceRequest serviceRequest = RecommendationFeedCreateServiceRequest.of(loginId,
                request);
        recommendationFeedWriteService.create(serviceRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(HttpStatus.CREATED.toString(), "추천 피드를 작성하였습니다.", null));
    }

    @GetMapping("/recommendation-feeds")
    public ResponseEntity<SuccessResponse<RecommendationFeedViewAllResponse>> getRecommendationFeedWithNoOffsetPaging(
            @RequestHeader("USER-ID") String loginId,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "cursor", required = false) Long cursor) {

        RecommendationFeedGetWithPagingServiceRequest request =
                RecommendationFeedGetWithPagingServiceRequest.of(cursor, limit, loginId);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "추천 피드를 조회하였습니다.",
                recommendationFeedReadService.findRecommendationFeedWithNoOffsetPaging(request)));
    }

    @DeleteMapping("/recommendation-feeds/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteRecommendationFeed(
            @RequestHeader("USER-ID") String loginId,
            @PathVariable("id") Long recommendationFeedId) {

        RecommendationFeedDeleteServiceRequest request =
                RecommendationFeedDeleteServiceRequest.of(recommendationFeedId, loginId);

        recommendationFeedWriteService.deleteRecommendationFeed(request);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "추천 피드를 삭제하였습니다.", null));
    }

    @PutMapping("/recommendation-feeds/{id}")
    public ResponseEntity<SuccessResponse<Void>> updateRecommendationFeed(
            @RequestHeader("USER-ID") String loginId,
            @PathVariable("id") Long recommendationFeedId,
            @Valid @RequestBody RecommendationFeedUpdateRequest request) {

        RecommendationFeedUpdateServiceRequest serviceRequest = RecommendationFeedUpdateServiceRequest.of(loginId,
                recommendationFeedId, request);

        recommendationFeedWriteService.updateRecommendationFeed(serviceRequest);
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "추천 피드를 수정했습니다.", null));
    }
}
