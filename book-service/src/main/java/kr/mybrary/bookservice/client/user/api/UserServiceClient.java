package kr.mybrary.bookservice.client.user.api;

import feign.Headers;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse;
import kr.mybrary.bookservice.client.user.dto.response.UserInfoServiceResponse.UserInfoList;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userClient")
public interface UserServiceClient {

    String DEFAULT_PROFILE_IMAGE_URL = "https://mybrary-user-service-resized.s3.ap-northeast-2.amazonaws.com/tiny-profile/profileImage/default.jpg";
    
    @GetMapping("/api/v1/users/info")
    @Headers("Content-Type: application/json")
    @Retry(name = "userServiceRetryConfig", fallbackMethod = "getUsersInfoFallback")
    @CircuitBreaker(name = "userServiceCircuitBreakerConfig", fallbackMethod = "getUsersInfoFallback")
    UserInfoServiceResponse getUsersInfo(@RequestParam("userId") List<String> userIds);

    default UserInfoServiceResponse getUsersInfoFallback(List<String> userIds, Exception ex) {
        return UserInfoServiceResponse.builder()
                .data(makeTemporaryResponse(userIds))
                .build();
    }

    private UserInfoList makeTemporaryResponse(List<String> userIds) {
        return UserInfoList.builder()
                .userInfoElements(userIds.stream()
                        .map(userId -> UserInfoServiceResponse.UserInfo.builder()
                                .userId(userId)
                                .nickname(makeTemporaryNickname(userId))
                                .profileImageUrl(DEFAULT_PROFILE_IMAGE_URL)
                                .build()
                        ).toList()
        ).build();
    }

    @NotNull
    private String makeTemporaryNickname(String userId) {
        if (userId.length() < 5) {
            return "user_" + userId.toLowerCase();
        }

        return "user_" + userId.substring(0, 5).toLowerCase();
    }
}
