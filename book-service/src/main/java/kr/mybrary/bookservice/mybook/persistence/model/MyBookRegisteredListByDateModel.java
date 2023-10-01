package kr.mybrary.bookservice.mybook.persistence.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyBookRegisteredListByDateModel {

    private String userId;
    private String title;
    private String thumbnailUrl;
    private String isbn13;
    private LocalDateTime registeredAt;

}
