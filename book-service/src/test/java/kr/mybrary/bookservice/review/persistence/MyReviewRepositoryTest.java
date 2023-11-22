package kr.mybrary.bookservice.review.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import kr.mybrary.bookservice.PersistenceTest;
import kr.mybrary.bookservice.book.BookFixture;
import kr.mybrary.bookservice.book.persistence.Book;
import kr.mybrary.bookservice.mybook.MyBookFixture;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.review.MyReviewFixture;
import kr.mybrary.bookservice.review.persistence.model.MyReviewElementByUserIdModel;
import kr.mybrary.bookservice.review.persistence.model.MyReviewElementModel;
import kr.mybrary.bookservice.review.persistence.model.MyReviewFromMyBookModel;
import kr.mybrary.bookservice.review.persistence.repository.MyReviewRepository;
import org.hibernate.proxy.HibernateProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@PersistenceTest
class MyReviewRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    MyReviewRepository myBookReviewRepository;

    @DisplayName("마이북과 도서를 통해 마이북 리뷰가 존재하는지 확인한다.")
    @Test
    void existsByMyBookAndBook() {

        // given
        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        MyBook myBook = entityManager.persist(
                MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookBuilder().book(book).build());

        entityManager.persist(MyReviewFixture.MY_BOOK_REVIEW_WITHOUT_RELATION.getMyBookReviewBuilder()
                .book(book).myBook(myBook).build());

        entityManager.flush();
        entityManager.clear();

        // when
        boolean result = myBookReviewRepository.existsByMyBook(myBook);

        // then
        assertAll(
                () -> assertThat(result).isTrue()
        );
    }

    @DisplayName("도서를 통해 마이북 리뷰를 조회한다.")
    @Test
    void findReviewsByBook() {

        // given
        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        MyBook myBook = entityManager.persist(
                MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookBuilder().book(book).build());

        MyReview myReview = entityManager.persist(MyReviewFixture.MY_BOOK_REVIEW_WITHOUT_RELATION
                .getMyBookReviewBuilder().book(book).myBook(myBook).build());

        entityManager.flush();
        entityManager.clear();

        // when
        List<MyReviewElementModel> reviewsByBook = myBookReviewRepository.findReviewsByBook(book);

        // then
        assertAll(
                () -> assertThat(reviewsByBook).hasSize(1),
                () -> {
                    assert reviewsByBook != null;
                    assertThat(reviewsByBook.get(0).getId()).isEqualTo(myReview.getId());
                    assertThat(reviewsByBook.get(0).getUserId()).isEqualTo(myBook.getUserId());
                    assertThat(reviewsByBook.get(0).getContent()).isEqualTo(myReview.getContent());
                    assertThat(reviewsByBook.get(0).getCreatedAt()).isNotNull();
                    assertThat(reviewsByBook.get(0).getStarRating()).isEqualTo(myReview.getStarRating());
                }
        );
    }

    @DisplayName("마이북을 통해 마이북 리뷰를 조회한다.")
    @Test
    void findReviewByMyBook() {

        // given
        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        MyBook myBook = entityManager.persist(
                MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookBuilder().book(book).build());

        MyReview myReview = entityManager.persist(MyReviewFixture.MY_BOOK_REVIEW_WITHOUT_RELATION
                .getMyBookReviewBuilder().book(book).myBook(myBook).build());

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<MyReviewFromMyBookModel> reviewByMyBook = myBookReviewRepository.findReviewByMyBook(myBook);

        // then
        assertAll(
                () -> {
                    assertThat(reviewByMyBook.isPresent()).isTrue();
                    assertThat(reviewByMyBook.get().getId()).isEqualTo(myReview.getId());
                    assertThat(reviewByMyBook.get().getContent()).isEqualTo(myReview.getContent());
                    assertThat(reviewByMyBook.get().getStarRating()).isEqualTo(myReview.getStarRating());
                    assertThat(reviewByMyBook.get().getCreatedAt()).isNotNull();
                    assertThat(reviewByMyBook.get().getUpdatedAt()).isNotNull();
                }
        );
    }

    @DisplayName("마이 리뷰 조회시 연관되어 있는 마이북을 함께 조회한다.")
    @Test
    void findByIdWithMyBookUsingFetchJoin() {

        // given
        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        MyBook myBook = entityManager.persist(
                MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookBuilder().book(book).build());

        MyReview myReview = entityManager.persist(MyReviewFixture.MY_BOOK_REVIEW_WITHOUT_RELATION
                .getMyBookReviewBuilder().book(book).myBook(myBook).build());

        entityManager.flush();
        entityManager.clear();

        // when
        myBookReviewRepository.findByIdWithMyBookUsingFetchJoin(myReview.getId()).ifPresent(review -> {

            // then
            assertAll(
                    () -> assertThat(review.getId()).isEqualTo(myReview.getId()),
                    () -> assertThat(review.getContent()).isEqualTo(myReview.getContent()),
                    () -> assertThat(review.getStarRating()).isEqualTo(myReview.getStarRating()),
                    () -> assertThat(review.getMyBook()).isNotInstanceOf(HibernateProxy.class)
            );
        });
    }

    @DisplayName("유저 아이디를 통해 마이북 리뷰를 조회한다.")
    @Test
    void findReviewsByUserId() {

        // given
        Book book_1 = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("ISBN13_1").isbn10("ISBN10_1").build());
        Book book_2 = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("ISBN13_2").isbn10("ISBN10_2").build());
        Book book_3 = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("ISBN13_3").isbn10("ISBN10_3").build());

        MyBook myBook_1 = entityManager.persist(
                MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookBuilder().userId("USER_ID").book(book_1).build());

        MyBook myBook_2 = entityManager.persist(
                MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookBuilder().userId("USER_ID").book(book_2).build());

        MyBook myBook_3 = entityManager.persist(
                MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookBuilder().userId("OTHER_ID").book(book_3).build());

        MyReview myReview_1 = entityManager.persist(MyReviewFixture.MY_BOOK_REVIEW_WITHOUT_RELATION
                .getMyBookReviewBuilder().book(book_1).myBook(myBook_1).build());

        MyReview myReview_2 = entityManager.persist(MyReviewFixture.MY_BOOK_REVIEW_WITHOUT_RELATION
                .getMyBookReviewBuilder().book(book_2).myBook(myBook_2).build());

        entityManager.persist(MyReviewFixture.MY_BOOK_REVIEW_WITHOUT_RELATION
                .getMyBookReviewBuilder().book(book_3).myBook(myBook_3).build());

        entityManager.flush();
        entityManager.clear();

        // when
        List<MyReviewElementByUserIdModel> models = myBookReviewRepository.findReviewsByUserId("USER_ID");

        // then
        assertAll(
                () -> assertThat(models).hasSize(2),
                () -> {
                    assert models != null;
                    assertThat(models.get(0).getReviewId()).isEqualTo(myReview_2.getId());
                    assertThat(models.get(0).getMyBookId()).isEqualTo(myBook_2.getId());
                    assertThat(models.get(0).getBookTitle()).isEqualTo(book_2.getTitle());
                    assertThat(models.get(0).getBookIsbn13()).isEqualTo(book_2.getIsbn13());
                    assertThat(models.get(0).getBookThumbnailUrl()).isEqualTo(book_2.getThumbnailUrl());
                    assertThat(models.get(0).getContent()).isEqualTo(myReview_2.getContent());
                    assertThat(models.get(0).getStarRating()).isEqualTo(myReview_2.getStarRating());
                    assertThat(models.get(0).getCreatedAt()).isNotNull();
                    assertThat(models.get(0).getUpdatedAt()).isNotNull();
                },
                () -> {
                    assert models != null;
                    assertThat(models.get(1).getReviewId()).isEqualTo(myReview_1.getId());
                    assertThat(models.get(1).getMyBookId()).isEqualTo(myBook_1.getId());
                    assertThat(models.get(1).getBookTitle()).isEqualTo(book_1.getTitle());
                    assertThat(models.get(1).getBookIsbn13()).isEqualTo(book_1.getIsbn13());
                    assertThat(models.get(1).getBookThumbnailUrl()).isEqualTo(book_1.getThumbnailUrl());
                    assertThat(models.get(1).getContent()).isEqualTo(myReview_1.getContent());
                    assertThat(models.get(1).getStarRating()).isEqualTo(myReview_1.getStarRating());
                    assertThat(models.get(1).getCreatedAt()).isNotNull();
                    assertThat(models.get(1).getUpdatedAt()).isNotNull();
                }
        );
    }
}