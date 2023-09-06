package kr.mybrary.bookservice.mybook.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoWithMyBookSettingForBookServiceRequest {

    private String isbn13;

    public static UserInfoWithMyBookSettingForBookServiceRequest of(String isbn13) {
        return UserInfoWithMyBookSettingForBookServiceRequest.builder()
                .isbn13(isbn13)
                .build();
    }

}
