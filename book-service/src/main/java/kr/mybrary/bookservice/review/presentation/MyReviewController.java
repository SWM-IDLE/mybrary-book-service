package kr.mybrary.bookservice.review.presentation;

import kr.mybrary.bookservice.global.dto.response.SuccessResponse;
import kr.mybrary.bookservice.review.domain.MyReviewReadService;
import kr.mybrary.bookservice.review.domain.MyReviewWriteService;
import kr.mybrary.bookservice.review.domain.dto.request.MyReviewDeleteServiceRequest;
import kr.mybrary.bookservice.review.domain.dto.request.MyReviewOfMyBookGetServiceRequest;
import kr.mybrary.bookservice.review.domain.dto.request.MyReviewOfUserIdGetServiceRequest;
import kr.mybrary.bookservice.review.domain.dto.request.MyReviewUpdateServiceRequest;
import kr.mybrary.bookservice.review.domain.dto.request.MyReviewsOfBookGetServiceRequest;
import kr.mybrary.bookservice.review.presentation.dto.request.MyReviewCreateRequest;
import kr.mybrary.bookservice.review.presentation.dto.request.MyReviewUpdateRequest;
import kr.mybrary.bookservice.review.presentation.dto.response.MyReviewOfMyBookGetResponse;
import kr.mybrary.bookservice.review.presentation.dto.response.MyReviewOfUserIdGetResponse;
import kr.mybrary.bookservice.review.presentation.dto.response.MyReviewUpdateResponse;
import kr.mybrary.bookservice.review.presentation.dto.response.MyReviewsOfBookGetResponse;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MyReviewController {

    private final MyReviewWriteService myReviewWriteService;
    private final MyReviewReadService myReviewReadService;

    @PostMapping("/mybooks/{myBookId}/reviews")
    public ResponseEntity<SuccessResponse<Void>> create(@RequestHeader("USER-ID") String loginId,
            @PathVariable Long myBookId,
            @RequestBody MyReviewCreateRequest request) {

        myReviewWriteService.create(request.toServiceRequest(loginId, myBookId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(HttpStatus.CREATED.toString(), "마이 리뷰를 작성했습니다.", null));
    }

    @GetMapping("/books/{isbn13}/reviews")
    public ResponseEntity<SuccessResponse<MyReviewsOfBookGetResponse>> getReviewsFromBook(@PathVariable String isbn13) {

        MyReviewsOfBookGetServiceRequest request = MyReviewsOfBookGetServiceRequest.of(isbn13);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(HttpStatus.OK.toString(), "도서의 리뷰 목록입니다.",
                        myReviewReadService.getReviewsFromBook(request)));
    }

    @GetMapping("/mybooks/{myBookId}/review")
    public ResponseEntity<SuccessResponse<MyReviewOfMyBookGetResponse>> getReviewFromMyBook(@PathVariable Long myBookId) {

        MyReviewOfMyBookGetServiceRequest request = MyReviewOfMyBookGetServiceRequest.of(myBookId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(HttpStatus.OK.toString(), "마이북에 대한 리뷰입니다.",
                        myReviewReadService.getReviewFromMyBook(request)));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<SuccessResponse<MyReviewUpdateResponse>> updateReview(@RequestHeader("USER-ID") String loginId,
                                                                                @PathVariable Long reviewId,
                                                                                @RequestBody MyReviewUpdateRequest request) {

        MyReviewUpdateServiceRequest serviceRequest = request.toServiceRequest(loginId, reviewId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(HttpStatus.OK.toString(), "마이 리뷰를 수정했습니다.",
                        myReviewWriteService.update(serviceRequest)));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<SuccessResponse<Void>> deleteReview(@RequestHeader("USER-ID") String loginId,
            @PathVariable Long reviewId) {

        MyReviewDeleteServiceRequest request = MyReviewDeleteServiceRequest.of(reviewId, loginId);
        myReviewWriteService.delete(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(HttpStatus.OK.toString(), "마이 리뷰를 삭제했습니다.", null));
    }

    @GetMapping("/reviews/users/{userId}")
    public ResponseEntity<SuccessResponse<MyReviewOfUserIdGetResponse>> getReviewsFromUserId(@PathVariable String userId) {

        MyReviewOfUserIdGetServiceRequest request = MyReviewOfUserIdGetServiceRequest.of(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(HttpStatus.OK.toString(), "유저의 전체 리뷰 목록입니다.", myReviewReadService.getReviewsFromUserId(request)));
    }
}
