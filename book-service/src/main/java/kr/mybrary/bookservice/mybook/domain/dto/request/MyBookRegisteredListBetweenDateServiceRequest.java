package kr.mybrary.bookservice.mybook.domain.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyBookRegisteredListBetweenDateServiceRequest {

    private LocalDate start;
    private LocalDate end;
}
