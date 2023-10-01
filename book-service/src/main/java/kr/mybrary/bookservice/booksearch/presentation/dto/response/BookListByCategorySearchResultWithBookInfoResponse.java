package kr.mybrary.bookservice.booksearch.presentation.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookListByCategorySearchResultWithBookInfoResponse {

    private List<Element> books;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Element {
        private String thumbnailUrl;
        private String isbn13;
        private String title;
        private String authors;
        private Double aladinStarRating;
    }

    public static BookListByCategorySearchResultWithBookInfoResponse of(List<Element> bookListByCategorySearchResultElement) {
        return BookListByCategorySearchResultWithBookInfoResponse.builder()
                .books(bookListByCategorySearchResultElement)
                .build();
    }
}
