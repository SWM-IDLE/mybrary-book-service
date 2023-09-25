package kr.mybrary.bookservice.book.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoWithInterestForBookServiceRequest {

    private String isbn13;

    public static UserInfoWithInterestForBookServiceRequest of(String isbn13) {
        return UserInfoWithInterestForBookServiceRequest.builder()
                .isbn13(isbn13)
                .build();
    }
}
