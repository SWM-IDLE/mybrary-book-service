package kr.mybrary.bookservice.mybook.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyBookRegisteredListBetweenDateResponse {

    private int totalCount;
    private List<MyBookElement> myBookRegisteredList;

    @Getter
    @Builder
    public static class MyBookElement {
        private String userId;
        private String nickname;
        private String profileImageUrl;

        private String title;
        private String thumbnailUrl;
        private String isbn13;
        private LocalDateTime registeredAt;
    }
}
