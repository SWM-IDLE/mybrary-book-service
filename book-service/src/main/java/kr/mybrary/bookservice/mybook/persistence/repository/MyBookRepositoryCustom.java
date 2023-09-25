package kr.mybrary.bookservice.mybook.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.mybrary.bookservice.book.persistence.Book;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.mybook.persistence.MyBookOrderType;
import kr.mybrary.bookservice.mybook.persistence.ReadStatus;
import kr.mybrary.bookservice.mybook.persistence.model.MyBookListDisplayElementModel;

public interface MyBookRepositoryCustom {

    List<MyBookListDisplayElementModel> findMyBookListDisplayElementModelsByUserId(String userId, MyBookOrderType myBookOrderType, ReadStatus readStatus);

    Long getBookRegistrationCountOfDay(LocalDate date);

    List<String> getReadCompletedUserIdListByBook(Book book);

    List<String> getMyBookUserIdListByBook(Book book);

    Optional<MyBook> getMyBookWithBookAndReviewUsingFetchJoin(Long mybookId);
}
