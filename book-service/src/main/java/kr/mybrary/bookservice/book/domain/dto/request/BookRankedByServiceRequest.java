package kr.mybrary.bookservice.book.domain.dto.request;

import kr.mybrary.bookservice.book.persistence.BookRankedOrderType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookRankedByServiceRequest {

    private BookRankedOrderType bookRankedOrderType;
    private Integer limit;

}
