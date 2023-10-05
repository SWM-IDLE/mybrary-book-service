package kr.mybrary.bookservice.booksearch.presentation.dto.response;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookListByCategorySearchResultWithBookInfoResponse implements Serializable {

    private List<Element> books;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Element implements Serializable {
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
