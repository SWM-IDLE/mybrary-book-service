package kr.mybrary.bookservice.recommend.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import kr.mybrary.bookservice.PersistenceTest;
import kr.mybrary.bookservice.book.BookFixture;
import kr.mybrary.bookservice.book.persistence.Book;
import kr.mybrary.bookservice.mybook.MyBookFixture;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import kr.mybrary.bookservice.recommend.persistence.RecommendationTarget;
import kr.mybrary.bookservice.recommend.persistence.RecommendationTargets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@PersistenceTest
class RecommendationFeedRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RecommendationFeedRepository recommendationFeedRepository;

    @DisplayName("추천 피드를 저장한다.")
    @Test
    void saveRecommendationFeed() {

        // given
        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        MyBook myBook = entityManager.persist(MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookWithBook(book));

        RecommendationFeed recommendationFeed = RecommendationFeed.builder()
                .userId("LOGIN_USER_ID")
                .myBook(myBook)
                .content("NEW CONTENT")
                .recommendationTargets(new RecommendationTargets(List.of(
                        RecommendationTarget.of("TARGET_NAME_1"),
                        RecommendationTarget.of("TARGET_NAME_2"),
                        RecommendationTarget.of("TARGET_NAME_3"),
                        RecommendationTarget.of("TARGET_NAME_4"),
                        RecommendationTarget.of("TARGET_NAME_5"))))
                .build();

        // when
        RecommendationFeed savedRecommendationFeed = recommendationFeedRepository.save(recommendationFeed);

        // then
        assertAll(
                () -> assertNotNull(savedRecommendationFeed.getId()),
                () -> assertThat(savedRecommendationFeed.getUserId()).isEqualTo("LOGIN_USER_ID"),
                () -> assertThat(savedRecommendationFeed.getContent()).isEqualTo("NEW CONTENT"),
                () -> assertThat(savedRecommendationFeed.getRecommendationTargets().getSize()).isEqualTo(5)
        );
    }

    @DisplayName("마이북 ID를 통해서 추천 피드가 존재하는지 확인한다.")
    @Test
    void checkExistByMyBookId() {

        // given
        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        MyBook myBook = entityManager.persist(MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookWithBook(book));

        RecommendationFeed recommendationFeed = RecommendationFeed.builder()
                .userId("LOGIN_USER_ID")
                .myBook(myBook)
                .content("NEW CONTENT")
                .recommendationTargets(new RecommendationTargets(List.of(
                        RecommendationTarget.of("TARGET_NAME_1"),
                        RecommendationTarget.of("TARGET_NAME_2"),
                        RecommendationTarget.of("TARGET_NAME_3"),
                        RecommendationTarget.of("TARGET_NAME_4"),
                        RecommendationTarget.of("TARGET_NAME_5"))))
                .build();

        recommendationFeedRepository.save(recommendationFeed);

        entityManager.flush();
        entityManager.clear();

        // when
        boolean isExist = recommendationFeedRepository.existsByMyBookId(myBook.getId());

        // then
        assertThat(isExist).isTrue();
    }

}