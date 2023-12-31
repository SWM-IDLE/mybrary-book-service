package kr.mybrary.bookservice.mybook.presentation;

import java.time.LocalDate;
import java.util.List;
import kr.mybrary.bookservice.global.dto.response.SuccessResponse;
import kr.mybrary.bookservice.mybook.domain.MyBookReadService;
import kr.mybrary.bookservice.mybook.domain.MyBookWriteService;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookDeleteServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookDetailServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookFindAllServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookFindByMeaningTagQuoteServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookReadCompletedStatusServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookRegisteredListBetweenDateServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookRegisteredStatusServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MybookUpdateServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.UserInfoWithMyBookSettingForBookServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.UserInfoWithReadCompletedForBookServiceRequest;
import kr.mybrary.bookservice.mybook.persistence.MyBookOrderType;
import kr.mybrary.bookservice.mybook.persistence.ReadStatus;
import kr.mybrary.bookservice.mybook.presentation.dto.request.MyBookCreateRequest;
import kr.mybrary.bookservice.mybook.presentation.dto.request.MyBookUpdateRequest;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookDetailResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookElementFromMeaningTagResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookElementResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookReadCompletedStatusResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookRegisteredListBetweenDateResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookRegisteredStatusResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookRegistrationCountResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookUpdateResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.UserInfoWithMyBookSettingForBookResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.UserInfoWithReadCompletedForBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
public class MyBookController {

    private final MyBookReadService myBookReadService;
    private final MyBookWriteService myBookWriteService;

    @PostMapping("/mybooks")
    public ResponseEntity<SuccessResponse<Void>> createMyBook(
            @RequestHeader("USER-ID") String loginId,
            @RequestBody MyBookCreateRequest request) {

        myBookWriteService.create(request.toServiceRequest(loginId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(HttpStatus.CREATED.toString(), "내 서재에 도서를 등록했습니다.", null));
    }

    @GetMapping("/users/{userId}/mybooks")
    public ResponseEntity<SuccessResponse<List<MyBookElementResponse>>> findAllMyBooks(
            @RequestHeader("USER-ID") String loginId,
            @PathVariable("userId") String userId,
            @RequestParam(value = "order", required = false, defaultValue = "none") String order,
            @RequestParam(value = "readStatus", required = false) String readStatus) {

        MyBookFindAllServiceRequest request = MyBookFindAllServiceRequest.of(loginId, userId, MyBookOrderType.of(order),
                ReadStatus.of(readStatus));

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "서재의 도서 목록입니다.",
                myBookReadService.findAllMyBooks(request)));
    }

    @GetMapping("/mybooks/{mybookId}")
    public ResponseEntity<SuccessResponse<MyBookDetailResponse>> findMyBookDetail(
            @RequestHeader("USER-ID") String loginId,
            @PathVariable("mybookId") Long mybookId) {

        MyBookDetailServiceRequest request = MyBookDetailServiceRequest.of(loginId, mybookId);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "마이북 상세보기입니다.",
                myBookReadService.findMyBookDetail(request)));
    }

    @DeleteMapping("/mybooks/{mybookId}")
    public ResponseEntity<SuccessResponse<Void>> deleteMyBook(
            @RequestHeader("USER-ID") String loginId,
            @PathVariable Long mybookId) {

        MyBookDeleteServiceRequest request = MyBookDeleteServiceRequest.of(loginId, mybookId);

        myBookWriteService.deleteMyBook(request);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "내 서재의 도서를 삭제했습니다.", null));
    }

    @PutMapping("/mybooks/{mybookId}")
    public ResponseEntity<SuccessResponse<MyBookUpdateResponse>> updateMyBookProperties(
            @RequestHeader("USER-ID") String loginId,
            @PathVariable Long mybookId,
            @RequestBody MyBookUpdateRequest request) {

        MybookUpdateServiceRequest serviceRequest = request.toServiceRequest(loginId, mybookId);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "내 서재의 마이북 속성을 수정했습니다.",
                myBookWriteService.updateMyBookProperties(serviceRequest)));
    }

    @GetMapping("/mybooks/meaning-tags/{meaningTagQuote}")
    public ResponseEntity<SuccessResponse<List<MyBookElementFromMeaningTagResponse>>> findMyBooksByMeaningTag(
            @RequestHeader("USER-ID") String loginId,
            @PathVariable String meaningTagQuote) {

        MyBookFindByMeaningTagQuoteServiceRequest request = MyBookFindByMeaningTagQuoteServiceRequest.of(loginId,
                meaningTagQuote);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "의미 태그를 통해서 마이북을 조회했습니다.",
                myBookReadService.findByMeaningTagQuote(request)));
    }

    @GetMapping("/mybooks/today-registration-count")
    public ResponseEntity<SuccessResponse<MyBookRegistrationCountResponse>> getTodayRegistrationCount() {

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "오늘의 마이북 등록 수입니다.",
                myBookReadService.getBookRegistrationCountOfToday()));
    }

    @GetMapping("/books/{isbn13}/mybook-registered-status")
    public ResponseEntity<SuccessResponse<MyBookRegisteredStatusResponse>> getMyBookRegisteredStatus(
            @RequestHeader("USER-ID") String loginId,
            @PathVariable String isbn13) {

        MyBookRegisteredStatusServiceRequest serviceRequest = MyBookRegisteredStatusServiceRequest.of(loginId, isbn13);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "해당 도서의 마이북 등록 상태 여부 입니다.",
                myBookReadService.getMyBookRegisteredStatus(serviceRequest)));
    }

    @GetMapping("/books/{isbn13}/read-complete-status")
    public ResponseEntity<SuccessResponse<MyBookReadCompletedStatusResponse>> getReadCompletedStatus(
            @RequestHeader("USER-ID") String loginId,
            @PathVariable String isbn13) {

        MyBookReadCompletedStatusServiceRequest serviceRequest = MyBookReadCompletedStatusServiceRequest.of(loginId, isbn13);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "해당 도서 완독 여부 입니다.",
                myBookReadService.getMyBookReadCompletedStatus(serviceRequest)));
    }

    @GetMapping("/books/{isbn13}/read-complete/userInfos")
    public ResponseEntity<SuccessResponse<UserInfoWithReadCompletedForBookResponse>> getUserInfoWithReadCompletedForBook(
            @PathVariable("isbn13") String isbn13) {

        UserInfoWithReadCompletedForBookServiceRequest request = UserInfoWithReadCompletedForBookServiceRequest.of(isbn13);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "도서를 완독한 유저 목록 조회에 성공했습니다.",
                myBookReadService.getUserIdWithReadCompletedListByBook(request)));
    }

    @GetMapping("/books/{isbn13}/mybook/userInfos")
    public ResponseEntity<SuccessResponse<UserInfoWithMyBookSettingForBookResponse>> getUserInfoWithMyBookSettingForBook(
            @PathVariable("isbn13") String isbn13) {

        UserInfoWithMyBookSettingForBookServiceRequest request = UserInfoWithMyBookSettingForBookServiceRequest.of(isbn13);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(), "도서를 마이북에 등록한 유저 목록 조회에 성공했습니다.",
                myBookReadService.getUserIdListWithMyBookSettingByBook(request)));
    }

    @GetMapping("/mybooks")
    public ResponseEntity<SuccessResponse<MyBookRegisteredListBetweenDateResponse>> getMyBookRegisteredListBetweenDate(
            @RequestParam(value = "start", required = false, defaultValue = "#{T(java.time.LocalDate).now()}") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam(value = "end", required = false, defaultValue = "#{T(java.time.LocalDate).now()}") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        MyBookRegisteredListBetweenDateServiceRequest request =
                MyBookRegisteredListBetweenDateServiceRequest.of(start, end);

        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK.toString(),
                String.format("%s ~ %s 동안 등록된 마이북 목록입니다.", start.toString(), end.toString()),
                myBookReadService.getMyBookRegisteredListBetweenDate(request)));
    }
}
