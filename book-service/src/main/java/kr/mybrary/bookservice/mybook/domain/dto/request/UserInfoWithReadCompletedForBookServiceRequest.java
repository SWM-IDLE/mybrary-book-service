package kr.mybrary.bookservice.mybook.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoWithReadCompletedForBookServiceRequest {

    private String isbn13;

    public static UserInfoWithReadCompletedForBookServiceRequest of(String isbn13) {
        return UserInfoWithReadCompletedForBookServiceRequest.builder()
                .isbn13(isbn13)
                .build();
    }

}
