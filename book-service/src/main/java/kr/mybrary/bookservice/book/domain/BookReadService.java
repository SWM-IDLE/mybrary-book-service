package kr.mybrary.bookservice.book.domain;

import java.util.List;
import java.util.Optional;
import kr.mybrary.bookservice.book.domain.dto.BookDtoMapper;
import kr.mybrary.bookservice.book.domain.dto.request.BookDetailServiceRequest;
import kr.mybrary.bookservice.book.domain.dto.request.BookRankedByServiceRequest;
import kr.mybrary.bookservice.book.domain.exception.BookAlreadyExistsException;
import kr.mybrary.bookservice.book.domain.exception.BookNotFoundException;
import kr.mybrary.bookservice.book.persistence.Book;
import kr.mybrary.bookservice.book.persistence.model.RankedBookElementModel;
import kr.mybrary.bookservice.book.persistence.repository.BookRepository;
import kr.mybrary.bookservice.book.presentation.dto.response.BookDetailResponse;
import kr.mybrary.bookservice.book.presentation.dto.response.BookRankedListByResponse;
import kr.mybrary.bookservice.booksearch.domain.PlatformBookSearchApiService;
import kr.mybrary.bookservice.booksearch.domain.dto.request.BookSearchServiceRequest;
import kr.mybrary.bookservice.booksearch.presentation.dto.response.BookSearchDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookReadService {

    private final BookRepository bookRepository;
    private final PlatformBookSearchApiService platformBookSearchApiService;
    private final BookWriteService bookWriteService;

    public BookDetailResponse getBookDetailByISBN(BookDetailServiceRequest request) {

        return bookRepository.findByISBNWithAuthorAndCategoryUsingFetchJoin(request.getIsbn10(), request.getIsbn13())
                .map(BookDtoMapper.INSTANCE::bookToDetailServiceResponse)
                .orElseGet(() -> {
                    BookSearchDetailResponse bookSearchDetailResponse = platformBookSearchApiService.searchBookDetailWithISBN(
                            BookSearchServiceRequest.of(request.getIsbn13()));

                    bookWriteService.create(BookDtoMapper.INSTANCE.bookSearchDetailToBookCreateServiceRequest(bookSearchDetailResponse));
                    return BookDtoMapper.INSTANCE.bookSearchDetailToDetailServiceResponse(bookSearchDetailResponse);
                });
    }

    @Async
    @Retryable(exclude = {BookAlreadyExistsException.class}, maxAttemptsExpression = "${retry.bookSave.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.bookSave.maxDelay}"))
    public void saveBookByISNB13WhenBookSearchKeyword(String isbn13) {
        bookRepository.findByIsbn13(isbn13)
                .ifPresentOrElse(
                        book -> {},
                        () -> {
                            BookSearchDetailResponse bookSearchDetailResponse = platformBookSearchApiService.searchBookDetailWithISBN(
                                    BookSearchServiceRequest.of(isbn13));

                            log.info("Call bookWriteService.create By bookSearchKeyword With Async: {}, {}", bookSearchDetailResponse.getTitle(), bookSearchDetailResponse.getIsbn13());
                            bookWriteService.create(BookDtoMapper.INSTANCE.bookSearchDetailToBookCreateServiceRequest(bookSearchDetailResponse));
                        }
                );
    }

    public Book getRegisteredBookByISBN13(String isbn13) {
        return bookRepository.findByIsbn13(isbn13).orElseThrow(BookNotFoundException::new);
    }

    public Optional<Book> findOptionalBookByISBN13(String isbn13) {
        return bookRepository.findByIsbn13(isbn13);
    }

    public BookRankedListByResponse getBookRankedListBy(BookRankedByServiceRequest request) {

        List<RankedBookElementModel> models = bookRepository.findRankedBookListBy(request.getLimit(),
                request.getBookRankedOrderType());

        return BookRankedListByResponse.builder()
                .books(models.stream()
                        .map(BookDtoMapper.INSTANCE::RankedBookElementModelToBookRankedElement)
                        .toList())
                .build();
    }
}
