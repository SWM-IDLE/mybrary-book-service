package kr.mybrary.bookservice.review.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyReviewOfUserIdGetServiceRequest {

    private String userId;

    public static MyReviewOfUserIdGetServiceRequest of(String userId) {
        return MyReviewOfUserIdGetServiceRequest.builder()
                .userId(userId)
                .build();
    }

}
