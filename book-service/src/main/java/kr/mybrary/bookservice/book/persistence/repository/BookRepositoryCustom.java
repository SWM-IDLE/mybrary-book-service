package kr.mybrary.bookservice.book.persistence.repository;

import java.util.List;
import kr.mybrary.bookservice.book.persistence.BookRankedOrderType;
import kr.mybrary.bookservice.book.persistence.model.RankedBookElementModel;

public interface BookRepositoryCustom {

    List<RankedBookElementModel> findRankedBookListBy(int limit, BookRankedOrderType bookRankedOrderType);

}
