package kr.mybrary.bookservice.mybook.presentation.dto.response;

import java.util.List;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoWithMyBookSetForBookResponse {

    private List<UserInfoElement> userInfos;

    @Getter
    @Builder
    public static class UserInfoElement {

        private String userId;
        private String nickname;
        private String profileImageUrl;
    }

    public static UserInfoWithMyBookSetForBookResponse of(UserInfoServiceResponse usersInfo) {
        return UserInfoWithMyBookSetForBookResponse.builder()
            .userInfos(usersInfo.getData().getUserInfoElements()
                    .stream()
                    .map(user -> UserInfoWithMyBookSetForBookResponse.UserInfoElement.builder()
                            .userId(user.getUserId())
                            .nickname(user.getNickname())
                            .profileImageUrl(user.getProfileImageUrl())
                            .build())
                    .toList())
            .build();
    }
}
