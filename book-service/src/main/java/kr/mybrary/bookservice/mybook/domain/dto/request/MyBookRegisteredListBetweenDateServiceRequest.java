package kr.mybrary.bookservice.mybook.domain.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyBookRegisteredListBetweenDateServiceRequest {

    private LocalDate start;
    private LocalDate end;

    public static MyBookRegisteredListBetweenDateServiceRequest of(LocalDate start, LocalDate end) {
        return MyBookRegisteredListBetweenDateServiceRequest.builder()
                .start(start)
                .end(end)
                .build();
    }
}
