package kr.mybrary.bookservice.mybook.presentation.dto.response;

import java.util.List;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoWithReadCompletedForBookResponse {

    private List<UserInfoElement> userInfos;

    @Getter
    @Builder
    public static class UserInfoElement {

        private String userId;
        private String nickname;
        private String profileImageUrl;
    }

    public static UserInfoWithReadCompletedForBookResponse of(UserInfoServiceResponse usersInfo) {
        return UserInfoWithReadCompletedForBookResponse.builder()
                .userInfos(usersInfo.getData().getUserInfoElements()
                        .stream()
                        .map(user -> UserInfoWithReadCompletedForBookResponse.UserInfoElement.builder()
                                .userId(user.getUserId())
                                .nickname(user.getNickname())
                                .profileImageUrl(user.getProfileImageUrl())
                                .build())
                        .toList())
                .build();
    }
}
