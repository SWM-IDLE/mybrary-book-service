package kr.mybrary.bookservice.booksearch.presentation.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookListByCategorySearchResultWithBookInfoResponse {

    private List<Element> books;

    @Getter
    @Builder
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
