package kr.mybrary.bookservice.mybook.presentation.dto.response;

import java.util.List;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoWithMyBookSettingForBookResponse {

    private List<UserInfoElement> userInfos;

    @Getter
    @Builder
    public static class UserInfoElement {

        private String userId;
        private String nickname;
        private String profileImageUrl;
    }

    public static UserInfoWithMyBookSettingForBookResponse of(UserInfoServiceResponse usersInfo) {
        return UserInfoWithMyBookSettingForBookResponse.builder()
            .userInfos(usersInfo.getData().getUserInfoElements()
                    .stream()
                    .map(user -> UserInfoWithMyBookSettingForBookResponse.UserInfoElement.builder()
                            .userId(user.getUserId())
                            .nickname(user.getNickname())
                            .profileImageUrl(user.getProfileImageUrl())
                            .build())
                    .toList())
            .build();
    }
}
