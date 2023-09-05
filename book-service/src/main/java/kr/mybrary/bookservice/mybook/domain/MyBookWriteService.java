package kr.mybrary.bookservice.mybook.domain;

import kr.mybrary.bookservice.book.domain.BookReadService;
import kr.mybrary.bookservice.book.persistence.Book;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookCreateServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookDeleteServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MybookUpdateServiceRequest;
import kr.mybrary.bookservice.mybook.domain.exception.MyBookAccessDeniedException;
import kr.mybrary.bookservice.mybook.domain.exception.MyBookAlreadyExistsException;
import kr.mybrary.bookservice.mybook.domain.exception.MyBookNotFoundException;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.mybook.persistence.repository.MyBookRepository;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookUpdateResponse;
import kr.mybrary.bookservice.tag.domain.MeaningTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MyBookWriteService {

    private final MyBookRepository myBookRepository;
    private final MeaningTagService meaningTagService;
    private final BookReadService bookReadService;

    public MyBook create(MyBookCreateServiceRequest request) {

        Book book = bookReadService.getRegisteredBookByISBN13(request.getIsbn13());
        checkBookAlreadyRegisteredAsMyBook(request.getUserId(), book);

        book.increaseHolderCount();
        MyBook myBook = MyBook.of(book, request.getUserId());
        return myBookRepository.save(myBook);
    }

    public void deleteMyBook(MyBookDeleteServiceRequest request) {

        MyBook myBook = findMyBookById(request.getMybookId());

        if (isOwnerSameAsRequester(myBook.getUserId(), request.getLoginId())) {
            throw new MyBookAccessDeniedException();
        }

        myBook.getBook().decreaseHolderCount();
        myBook.deleteMyBook();
    }

    public MyBookUpdateResponse updateMyBookProperties(MybookUpdateServiceRequest request) {

        MyBook myBook = findMyBookById(request.getMyBookId());

        if (isOwnerSameAsRequester(myBook.getUserId(), request.getLoginId())) {
            throw new MyBookAccessDeniedException();
        }

        myBook.updateFromUpdateRequest(request);
        meaningTagService.assignMeaningTag(request.toMeaningTagAssignServiceRequest(myBook));

        return MyBookUpdateResponse.of(myBook, request.getMeaningTag());
    }

    private void checkBookAlreadyRegisteredAsMyBook(String userId, Book book) {
        if (myBookRepository.existsByUserIdAndBook(userId, book)) {
            throw new MyBookAlreadyExistsException();
        }
    }

    private static boolean isOwnerSameAsRequester(String ownerId, String requesterId) {
        return !ownerId.equals(requesterId);
    }

    private MyBook findMyBookById(Long myBookId) {
        return myBookRepository.findById(myBookId).orElseThrow(MyBookNotFoundException::new);
    }
}
