package kr.mybrary.bookservice.book.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import kr.mybrary.bookservice.PersistenceTest;
import kr.mybrary.bookservice.book.BookFixture;
import kr.mybrary.bookservice.book.persistence.bookInfo.Author;
import kr.mybrary.bookservice.book.persistence.bookInfo.BookAuthor;
import kr.mybrary.bookservice.book.persistence.bookInfo.BookCategory;
import kr.mybrary.bookservice.book.persistence.bookInfo.BookTranslator;
import kr.mybrary.bookservice.book.persistence.bookInfo.Translator;
import kr.mybrary.bookservice.book.persistence.model.RankedBookElementModel;
import kr.mybrary.bookservice.book.persistence.repository.BookRepository;
import org.hibernate.proxy.HibernateProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@PersistenceTest
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    TestEntityManager entityManager;

    @DisplayName("isbn10 또는 isbn13으로 도서를 조회한다.")
    @Test
    void findByIsbn10OrIsbn13() {

        // given
        Book book = BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook();

        // when
        bookRepository.save(book);

        entityManager.flush();
        entityManager.clear();

        // then
        assertAll(
                () -> assertThat(bookRepository.findByIsbn10OrIsbn13(book.getIsbn10(), "").isPresent()).isTrue(),
                () -> assertThat(bookRepository.findByIsbn10OrIsbn13("", book.getIsbn13()).isPresent()).isTrue()
        );
    }

    @DisplayName("isbn13으로 도서를 조회한다.")
    @Test
    void findByIsbn13() {

        // given
        Book book = BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook();

        // when
        bookRepository.save(book);

        entityManager.flush();
        entityManager.clear();

        // then
        assertAll(
                () -> assertThat(bookRepository.findByIsbn13(book.getIsbn13()).isPresent()).isTrue()
        );
    }

    @DisplayName("isbn13으로 도서를 조회시, 도서 저자, 도서 번역가, 도서 카테고리를 함께 조회한다.")
    @Test
    void findByISBN13WithAllDetailUsingFetchJoin() {

        // given
        Author author = entityManager.persist(Author.builder().aid(11).name("테스트 저자").build());
        Translator translator = entityManager.persist(Translator.builder().tid(12).name("테스트 번역가").build());
        BookCategory bookCategory = entityManager.persist(BookCategory.builder().cid(13).name("테스트 카테고리").build());

        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder()
                .bookCategory(bookCategory)
                .build());

        entityManager.persist(BookTranslator.builder().book(book).translator(translator).build());
        entityManager.persist(BookAuthor.builder().book(book).author(author).build());

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<Book> foundBook = bookRepository.findByISBNWithAuthorAndCategoryUsingFetchJoin(book.getIsbn10(), book.getIsbn13());

        // then
        assertAll(
                () -> {
                    assertThat(foundBook).isNotEmpty();
                    assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
                    assertThat(foundBook.get().getBookCategory() instanceof HibernateProxy).isFalse();
                    assertThat(foundBook.get().getBookAuthors().get(0).getAuthor() instanceof HibernateProxy).isFalse();
                    assertThat(foundBook.get().getBookTranslators().get(0).getTranslator() instanceof HibernateProxy).isTrue();
                }
        );
    }

    @DisplayName("가장 많이 보유 중인 도서 순서대로 5개 조회한다.")
    @Test
    void findRankedBookListByHolderCount() {

        // given
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-1").isbn10("isbn10-1").holderCount(1).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-2").isbn10("isbn10-2").holderCount(2).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-3").isbn10("isbn10-3").holderCount(3).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-4").isbn10("isbn10-4").holderCount(4).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-5").isbn10("isbn10-5").holderCount(5).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-6").isbn10("isbn10-6").holderCount(0).build());

        entityManager.flush();
        entityManager.clear();

        // when
        List<RankedBookElementModel> rankedBookListBy = bookRepository.findRankedBookListBy(5,
                BookRankedOrderType.HOLDER);

        // then
        assertAll(
                () -> assertThat(rankedBookListBy.size()).isEqualTo(5),
                () -> assertThat(rankedBookListBy.get(0).getIsbn13()).isEqualTo("isbn13-5"),
                () -> assertThat(rankedBookListBy.get(1).getIsbn13()).isEqualTo("isbn13-4"),
                () -> assertThat(rankedBookListBy.get(2).getIsbn13()).isEqualTo("isbn13-3"),
                () -> assertThat(rankedBookListBy.get(3).getIsbn13()).isEqualTo("isbn13-2"),
                () -> assertThat(rankedBookListBy.get(4).getIsbn13()).isEqualTo("isbn13-1")
        );
    }

    @DisplayName("가장 리뷰가 많은 도서 순서대로 5개 조회한다.")
    @Test
    void findRankedBookListByReviewCount() {

        // given
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-1").isbn10("isbn10-1").reviewCount(3).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-2").isbn10("isbn10-2").reviewCount(1).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-3").isbn10("isbn10-3").reviewCount(2).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-4").isbn10("isbn10-4").reviewCount(4).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-5").isbn10("isbn10-5").reviewCount(5).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-6").isbn10("isbn10-6").reviewCount(6).build());

        entityManager.flush();
        entityManager.clear();

        // when
        List<RankedBookElementModel> rankedBookListBy = bookRepository.findRankedBookListBy(5,
                BookRankedOrderType.REVIEW);

        // then
        assertAll(
                () -> assertThat(rankedBookListBy.size()).isEqualTo(5),
                () -> assertThat(rankedBookListBy.get(0).getIsbn13()).isEqualTo("isbn13-6"),
                () -> assertThat(rankedBookListBy.get(1).getIsbn13()).isEqualTo("isbn13-5"),
                () -> assertThat(rankedBookListBy.get(2).getIsbn13()).isEqualTo("isbn13-4"),
                () -> assertThat(rankedBookListBy.get(3).getIsbn13()).isEqualTo("isbn13-1"),
                () -> assertThat(rankedBookListBy.get(4).getIsbn13()).isEqualTo("isbn13-3")
        );
    }

    @DisplayName("가장 리뷰 별점이 많은 순서대로 5개 조회한다.")
    @Test
    void findRankedBookListByStarRating() {

        // given
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-1").isbn10("isbn10-1").starRating(3.0).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-2").isbn10("isbn10-2").starRating(1.0).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-3").isbn10("isbn10-3").starRating(2.0).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-4").isbn10("isbn10-4").starRating(4.0).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-5").isbn10("isbn10-5").starRating(5.0).build());
        entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13-6").isbn10("isbn10-6").starRating(6.0).build());

        entityManager.flush();
        entityManager.clear();

        // when
        List<RankedBookElementModel> rankedBookListBy = bookRepository.findRankedBookListBy(5,
                BookRankedOrderType.STAR);

        // then
        assertAll(
                () -> assertThat(rankedBookListBy.size()).isEqualTo(5),
                () -> assertThat(rankedBookListBy.get(0).getIsbn13()).isEqualTo("isbn13-6"),
                () -> assertThat(rankedBookListBy.get(1).getIsbn13()).isEqualTo("isbn13-5"),
                () -> assertThat(rankedBookListBy.get(2).getIsbn13()).isEqualTo("isbn13-4"),
                () -> assertThat(rankedBookListBy.get(3).getIsbn13()).isEqualTo("isbn13-1"),
                () -> assertThat(rankedBookListBy.get(4).getIsbn13()).isEqualTo("isbn13-3")
        );
    }
}