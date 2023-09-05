package kr.mybrary.bookservice.mybook.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoWithMyBookSetForBookServiceRequest {

    private String isbn13;

    public static UserInfoWithMyBookSetForBookServiceRequest of(String isbn13) {
        return UserInfoWithMyBookSetForBookServiceRequest.builder()
                .isbn13(isbn13)
                .build();
    }

}
