package kr.mybrary.bookservice.book.persistence.repository.interest;

import java.util.List;
import kr.mybrary.bookservice.book.persistence.Book;
import kr.mybrary.bookservice.book.persistence.BookInterest;
import kr.mybrary.bookservice.book.persistence.BookOrderType;

public interface BookInterestRepositoryCustom {

    List<BookInterest> findAllByUserIdWithBook(String userId, BookOrderType bookOrderType);

    List<String> findUserIdsByBook(Book book);

}
