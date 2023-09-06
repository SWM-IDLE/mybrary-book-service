package kr.mybrary.bookservice.book.presentation.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoWithInterestForBookResponse {

    private List<UserInfoElement> userInfos;

    @Getter
    @Builder
    public static class UserInfoElement {

        private String userId;
        private String nickname;
        private String profileImageUrl;
    }
}
