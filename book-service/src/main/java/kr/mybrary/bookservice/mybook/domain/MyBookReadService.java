package kr.mybrary.bookservice.mybook.domain;

import java.time.LocalDate;
import java.util.List;
import kr.mybrary.bookservice.book.domain.BookReadService;
import kr.mybrary.bookservice.mybook.domain.dto.MyBookDtoMapper;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookDetailServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookFindAllServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookFindByMeaningTagQuoteServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookReadCompletedStatusServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookRegisteredStatusServiceRequest;
import kr.mybrary.bookservice.mybook.domain.exception.MyBookAccessDeniedException;
import kr.mybrary.bookservice.mybook.domain.exception.MyBookNotFoundException;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.mybook.persistence.ReadStatus;
import kr.mybrary.bookservice.mybook.persistence.model.MyBookListDisplayElementModel;
import kr.mybrary.bookservice.mybook.persistence.repository.MyBookRepository;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookDetailResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookElementFromMeaningTagResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookElementResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookReadCompletedStatusResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookRegisteredStatusResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookRegistrationCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyBookReadService {

    private final MyBookRepository myBookRepository;
    private final BookReadService bookReadService;

    public List<MyBookElementResponse> findAllMyBooks(MyBookFindAllServiceRequest request) {

        List<MyBookListDisplayElementModel> model = myBookRepository.findMyBookListDisplayElementModelsByUserId(
                request.getUserId(),
                request.getMyBookOrderType(),
                request.getReadStatus());

        return model.stream()
                .filter(myBook -> request.getUserId().equals(request.getLoginId()) || myBook.isShowable())
                .map(MyBookDtoMapper.INSTANCE::modelToMyBookElementResponse)
                .toList();
    }

    public MyBookDetailResponse findMyBookDetail(MyBookDetailServiceRequest request) {

        MyBook myBook = myBookRepository.findMyBookDetailUsingFetchJoin(request.getMybookId())
                .orElseThrow(MyBookNotFoundException::new);

        if (myBook.isPrivate() && isOwnerSameAsRequester(myBook.getUserId(), request.getLoginId())) {
            throw new MyBookAccessDeniedException();
        }

        return MyBookDtoMapper.INSTANCE.entityToMyBookDetailResponse(myBook);
    }

    public List<MyBookElementFromMeaningTagResponse> findByMeaningTagQuote(
            MyBookFindByMeaningTagQuoteServiceRequest request) {

        return myBookRepository.findByMeaningTagQuote(request.getQuote())
                .stream()
                .filter(myBook -> myBook.getUserId().equals(request.getLoginId()) || myBook.isShowable())
                .map(MyBookDtoMapper.INSTANCE::entityToMyBookElementFromMeaningTagResponse)
                .toList();
    }

    public MyBook findMyBookByIdWithBook(Long myBookId) {
        return myBookRepository.findByIdWithBook(myBookId).orElseThrow(MyBookNotFoundException::new);
    }

    public MyBookRegistrationCountResponse getBookRegistrationCountOfToday() {

        return MyBookRegistrationCountResponse.of(myBookRepository.getBookRegistrationCountOfDay(LocalDate.now()));
    }

    public MyBookRegisteredStatusResponse getMyBookRegisteredStatus(
            MyBookRegisteredStatusServiceRequest request) {

        return bookReadService.findOptionalBookByISBN13(request.getIsbn13())
                .map(book -> MyBookRegisteredStatusResponse.of(myBookRepository.existsByUserIdAndBook(request.getLoginId(), book)))
                .orElseGet(() -> MyBookRegisteredStatusResponse.of(false));
    }

    public MyBookReadCompletedStatusResponse getMyBookReadCompletedStatus(
            MyBookReadCompletedStatusServiceRequest request) {

        return bookReadService.findOptionalBookByISBN13(request.getIsbn13())
                .map(book -> myBookRepository.findByUserIdAndBook(request.getLoginId(), book)
                        .map(myBook -> MyBookReadCompletedStatusResponse.of(myBook.getReadStatus() == ReadStatus.COMPLETED))
                        .orElseGet(() -> MyBookReadCompletedStatusResponse.of(false)))
                .orElseGet(() -> MyBookReadCompletedStatusResponse.of(false));
    }

    private static boolean isOwnerSameAsRequester(String ownerId, String requesterId) {
        return !ownerId.equals(requesterId);
    }

    public MyBook findMyBookById(Long myBookId) {
        return myBookRepository.findById(myBookId).orElseThrow(MyBookNotFoundException::new);
    }
}
