package kr.mybrary.bookservice.client.user.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceClientTest {

    private final String DEFAULT_PROFILE_IMAGE_URL = "https://mybrary-user-service-resized.s3.ap-northeast-2.amazonaws.com/tiny-profile/profileImage/default.jpg";

    @DisplayName("feignClient로 유저 정보 조회가 실패하면, 임시 유저 정보를 반환한다. (fallback)")
    @Test
    void getUsersInfoFallback() {

        // given
        List<String> userIds = List.of("USER_ID_1", "USER_ID_2", "US_3");
        UserServiceClient userServiceClient = userIds1 -> null;

        // when
        UserInfoServiceResponse response = userServiceClient.getUsersInfoFallback(userIds, new Exception());

        // then
        assertAll(
                () -> assertThat(response.getData().getUserInfoElements()).hasSize(3),
                () -> assertThat(response.getData().getUserInfoElements()).extracting("userId").containsExactly("USER_ID_1", "USER_ID_2", "US_3"),
                () -> assertThat(response.getData().getUserInfoElements()).extracting("nickname").containsExactly("user_user_", "user_user_", "user_us_3"),
                () -> assertThat(response.getData().getUserInfoElements()).extracting("profileImageUrl").containsExactly(DEFAULT_PROFILE_IMAGE_URL, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_PROFILE_IMAGE_URL)
        );
    }
}