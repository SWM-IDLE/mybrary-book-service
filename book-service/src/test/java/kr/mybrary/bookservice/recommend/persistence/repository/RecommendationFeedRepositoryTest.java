package kr.mybrary.bookservice.recommend.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.stream.IntStream;
import kr.mybrary.bookservice.PersistenceTest;
import kr.mybrary.bookservice.book.BookFixture;
import kr.mybrary.bookservice.book.persistence.Book;
import kr.mybrary.bookservice.book.persistence.BookInterest;
import kr.mybrary.bookservice.book.persistence.bookInfo.Author;
import kr.mybrary.bookservice.book.persistence.bookInfo.BookAuthor;
import kr.mybrary.bookservice.mybook.MyBookFixture;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.recommend.RecommendationFeedDtoTestData;
import kr.mybrary.bookservice.recommend.domain.dto.request.RecommendationFeedUpdateServiceRequest;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import kr.mybrary.bookservice.recommend.persistence.RecommendationTarget;
import kr.mybrary.bookservice.recommend.persistence.RecommendationTargets;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfUserViewModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;
import org.hibernate.proxy.HibernateProxy;
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

    @Autowired
    RecommendationFeedTargetRepository recommendationFeedTargetRepository;

    @DisplayName("추천 피드를 저장한다.")
    @Test
    void saveRecommendationFeed() {

        // given
        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        MyBook myBook = entityManager.persist(MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookWithBook(book));

        RecommendationTargets recommendationTargets = new RecommendationTargets(List.of(
                RecommendationTarget.of("TARGET_NAME_1"),
                RecommendationTarget.of("TARGET_NAME_2"),
                RecommendationTarget.of("TARGET_NAME_3"),
                RecommendationTarget.of("TARGET_NAME_4"),
                RecommendationTarget.of("TARGET_NAME_5")));

        RecommendationFeed recommendationFeed = RecommendationFeed.builder()
                .userId("LOGIN_USER_ID")
                .myBook(myBook)
                .content("NEW CONTENT")
                .build();

        recommendationFeed.addRecommendationFeedTarget(recommendationTargets.getFeedRecommendationTargets());

        entityManager.flush();
        entityManager.clear();

        // when
        RecommendationFeed savedRecommendationFeed = recommendationFeedRepository.save(recommendationFeed);

        // then
        List<RecommendationTarget> savedRecommendationTargets = recommendationFeedTargetRepository.findAll();
        assertAll(
                () -> assertNotNull(savedRecommendationFeed.getId()),
                () -> assertThat(savedRecommendationFeed.getUserId()).isEqualTo("LOGIN_USER_ID"),
                () -> assertThat(savedRecommendationFeed.getContent()).isEqualTo("NEW CONTENT"),
                () -> assertThat(savedRecommendationFeed.getRecommendationTargets().getSize()).isEqualTo(5),
                () -> assertThat(savedRecommendationTargets).hasSize(5),
                () -> assertThat(savedRecommendationTargets).extracting("recommendationFeed")
                        .containsExactlyInAnyOrder(savedRecommendationFeed, savedRecommendationFeed, savedRecommendationFeed,
                                savedRecommendationFeed, savedRecommendationFeed)
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

    @DisplayName("1번째부터 10번째의 추천 피드를 조회한다. paging 처리")
    @Test
    void getRecentRecommendationFeedView() {

        // given
        Author author_1 = entityManager.persist(Author.builder().aid(11).name("테스트 저자 1").build());
        Author author_2 = entityManager.persist(Author.builder().aid(12).name("테스트 저자 2").build());
        Author author_3 = entityManager.persist(Author.builder().aid(13).name("테스트 저자 3").build());

        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        entityManager.persist(BookAuthor.builder().book(book).author(author_1).build());
        entityManager.persist(BookAuthor.builder().book(book).author(author_2).build());
        entityManager.persist(BookAuthor.builder().book(book).author(author_3).build());

        IntStream.range(1, 21)
                .forEach(i -> {
                    MyBook myBook = entityManager.persist(MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookWithBook(book));

                    RecommendationFeed recommendationFeed = recommendationFeedRepository.save(RecommendationFeed.builder()
                            .userId("LOGIN_USER_ID_" + i)
                            .myBook(myBook)
                            .content("NEW_CONTENT_" + i)
                            .build());

                    entityManager.persist(RecommendationTarget.builder()
                            .recommendationFeed(recommendationFeed)
                            .targetName("TARGET_NAME_" + i)
                            .build());
                });

        entityManager.flush();
        entityManager.clear();

        // when
        List<RecommendationFeedViewAllModel> recommendationFeedViewAll =
                recommendationFeedRepository.getRecommendationFeedViewAll(null, 10, "LOGIN_USER_ID");

        // then
        assertAll(
                () -> assertThat(recommendationFeedViewAll).hasSize(10),
                () -> assertThat(recommendationFeedViewAll.get(0).getRecommendationTargets()).hasSize(1),
                () -> assertThat(recommendationFeedViewAll.get(0).getContent()).isEqualTo("NEW_CONTENT_20"),
                () -> assertThat(recommendationFeedViewAll.get(0).getRecommendationTargets()).extracting("targetName")
                        .containsExactly("TARGET_NAME_20"),
                () -> assertThat(recommendationFeedViewAll.get(0).getBookAuthors()).extracting("name")
                        .containsExactly("테스트 저자 1", "테스트 저자 2", "테스트 저자 3"),
                () -> assertThat(recommendationFeedViewAll.get(9).getRecommendationTargets()).hasSize(1),
                () -> assertThat(recommendationFeedViewAll.get(9).getContent()).isEqualTo("NEW_CONTENT_11"),
                () -> assertThat(recommendationFeedViewAll.get(9).getRecommendationTargets()).extracting("targetName")
                        .containsExactly("TARGET_NAME_11"),
                () -> assertThat(recommendationFeedViewAll.get(9).getBookAuthors()).extracting("name")
                        .containsExactly("테스트 저자 1", "테스트 저자 2", "테스트 저자 3")
        );
    }

    @DisplayName("11번째부터 20번째의 추천 피드를 조회한다. paging 처리")
    @Test
    void getRecommendationFeedViewOf10to20() {

        // given
        Author author_1 = entityManager.persist(Author.builder().aid(11).name("테스트 저자 1").build());
        Author author_2 = entityManager.persist(Author.builder().aid(12).name("테스트 저자 2").build());
        Author author_3 = entityManager.persist(Author.builder().aid(13).name("테스트 저자 3").build());

        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        entityManager.persist(BookAuthor.builder().book(book).author(author_1).build());
        entityManager.persist(BookAuthor.builder().book(book).author(author_2).build());
        entityManager.persist(BookAuthor.builder().book(book).author(author_3).build());

        IntStream.range(1, 21)
                .forEach(i -> {
                    MyBook myBook = entityManager.persist(MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookWithBook(book));

                    RecommendationFeed recommendationFeed = recommendationFeedRepository.save(RecommendationFeed.builder()
                            .userId("LOGIN_USER_ID_" + i)
                            .myBook(myBook)
                            .content("NEW_CONTENT_" + i)
                            .build());

                    entityManager.persist(RecommendationTarget.builder()
                            .recommendationFeed(recommendationFeed)
                            .targetName("TARGET_NAME_" + i)
                            .build());
                });

        entityManager.flush();
        entityManager.clear();

        List<RecommendationFeedViewAllModel> previousRecommendationFeedViewAll =
                recommendationFeedRepository.getRecommendationFeedViewAll(null, 10, "LOGIN_USER_ID");
        Long lastPreviousRecommendationFeedId = previousRecommendationFeedViewAll.get(9).getRecommendationFeedId();

        // when
        List<RecommendationFeedViewAllModel> recommendationFeedViewAll =
                recommendationFeedRepository.getRecommendationFeedViewAll(lastPreviousRecommendationFeedId, 10, "LOGIN_USER_ID");

        // then
        assertAll(
                () -> assertThat(recommendationFeedViewAll).hasSize(10),
                () -> assertThat(recommendationFeedViewAll.get(0).getRecommendationTargets()).hasSize(1),
                () -> assertThat(recommendationFeedViewAll.get(0).getContent()).isEqualTo("NEW_CONTENT_10"),
                () -> assertThat(recommendationFeedViewAll.get(0).getRecommendationTargets()).extracting("targetName")
                        .containsExactly("TARGET_NAME_10"),
                () -> assertThat(recommendationFeedViewAll.get(0).getBookAuthors()).extracting("name")
                        .containsExactly("테스트 저자 1", "테스트 저자 2", "테스트 저자 3"),
                () -> assertThat(recommendationFeedViewAll.get(9).getRecommendationTargets()).hasSize(1),
                () -> assertThat(recommendationFeedViewAll.get(9).getContent()).isEqualTo("NEW_CONTENT_1"),
                () -> assertThat(recommendationFeedViewAll.get(9).getRecommendationTargets()).extracting("targetName")
                        .containsExactly("TARGET_NAME_1"),
                () -> assertThat(recommendationFeedViewAll.get(9).getBookAuthors()).extracting("name")
                        .containsExactly("테스트 저자 1", "테스트 저자 2", "테스트 저자 3")
        );
    }

    @DisplayName("추천 피드를 삭제한다.")
    @Test
    void deleteRecommendationFeed() {

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

        entityManager.flush();
        entityManager.clear();
        RecommendationFeed savedRecommendationFeed = recommendationFeedRepository.save(recommendationFeed);

        // when
        recommendationFeedRepository.delete(savedRecommendationFeed);

        // then
        assertAll(
                () -> assertThat(recommendationFeedRepository.findById(savedRecommendationFeed.getId())).isEmpty(),
                () -> assertThat(recommendationFeedTargetRepository.findAll()).isEmpty()
        );
    }

    @DisplayName("추천 피드 조회시, 페치조인을 통해 추천 피드 타겟도 조회한다.")
    @Test
    void getRecommendationFeedWithTargetsUsingFetchJoin() {

        // given
        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        MyBook myBook = entityManager.persist(MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookWithBook(book));

        RecommendationFeed recommendationFeed = RecommendationFeed.builder()
                .userId("LOGIN_USER_ID")
                .myBook(myBook)
                .content("NEW CONTENT")
                .build();

        List<RecommendationTarget> recommendationTargets = (List.of(
                RecommendationTarget.of("TARGET_NAME_1"),
                RecommendationTarget.of("TARGET_NAME_2"),
                RecommendationTarget.of("TARGET_NAME_3"),
                RecommendationTarget.of("TARGET_NAME_4"),
                RecommendationTarget.of("TARGET_NAME_5")));

        recommendationFeed.addRecommendationFeedTarget(recommendationTargets);

        RecommendationFeed savedRecommendationFeed = recommendationFeedRepository.save(recommendationFeed);

        entityManager.flush();
        entityManager.clear();

        RecommendationFeed recommendationFeedWithTargets = recommendationFeedRepository.getRecommendationFeedWithTargets(
                savedRecommendationFeed.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(recommendationFeedWithTargets.getRecommendationTargets()
                        .getFeedRecommendationTargets()).isNotInstanceOf(HibernateProxy.class),
                () -> assertThat(recommendationFeedWithTargets.getContent()).isEqualTo(savedRecommendationFeed.getContent()),
                () -> assertThat(recommendationFeedWithTargets.getRecommendationTargets().getFeedRecommendationTargets())
                        .extracting("targetName")
                        .containsExactlyInAnyOrder("TARGET_NAME_1", "TARGET_NAME_2", "TARGET_NAME_3", "TARGET_NAME_4", "TARGET_NAME_5")
        );
    }

    @DisplayName("추천 피드를 수정한다.")
    @Test
    void updateRecommendationFeed() {

        // given
        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        MyBook myBook = entityManager.persist(MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookWithBook(book));

        RecommendationFeed recommendationFeed = RecommendationFeed.builder()
                .userId("LOGIN_USER_ID")
                .myBook(myBook)
                .content("NEW CONTENT")
                .build();

        List<RecommendationTarget> recommendationTargets = (List.of(
                RecommendationTarget.of("TARGET_NAME_1"),
                RecommendationTarget.of("TARGET_NAME_2"),
                RecommendationTarget.of("TARGET_NAME_3"),
                RecommendationTarget.of("TARGET_NAME_4"),
                RecommendationTarget.of("TARGET_NAME_5")));

        recommendationFeed.addRecommendationFeedTarget(recommendationTargets);
        RecommendationFeed savedRecommendationFeed = recommendationFeedRepository.save(recommendationFeed);

        entityManager.flush();
        entityManager.clear();

        // when
        RecommendationFeed findRecommendationFeed = recommendationFeedRepository.findById(savedRecommendationFeed.getId())
                .orElseThrow();
        RecommendationFeedUpdateServiceRequest request = RecommendationFeedDtoTestData.createRecommendationFeedUpdateServiceRequest();
        findRecommendationFeed.update(request);

        entityManager.flush();
        entityManager.clear();

        RecommendationFeed updatedRecommendationFeed = recommendationFeedRepository.findById(findRecommendationFeed.getId())
                .orElseThrow();

        // then
        assertAll(
                () -> assertThat(updatedRecommendationFeed.getContent()).isEqualTo(request.getContent()),
                () -> assertThat(updatedRecommendationFeed.getRecommendationTargets().getSize()).isEqualTo(2),
                () -> assertThat(updatedRecommendationFeed.getRecommendationTargets().getFeedRecommendationTargets()).extracting("targetName")
                        .containsExactly("New Target 1", "New Target 2"),
                () -> assertThat(recommendationFeedTargetRepository.findAll()).hasSize(2).extracting("targetName")
                        .containsExactly("New Target 1", "New Target 2")
        );
    }

    @DisplayName("추천 피드를 조회 시, 요청한 유저의 관심 도서에 따라 추천 피드 도서의 관심 여부를 설정한다.")
    @Test
    void getRecentRecommendationFeedViewWithInterested() {

        // given
        Author author_1 = entityManager.persist(Author.builder().aid(11).name("테스트 저자 1").build());
        Author author_2 = entityManager.persist(Author.builder().aid(12).name("테스트 저자 2").build());
        Author author_3 = entityManager.persist(Author.builder().aid(13).name("테스트 저자 3").build());

        Book book_1 = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        Book book_2 = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBookBuilder().isbn13("isbn13_1").isbn10("isbn10_1").build());
        entityManager.persist(BookAuthor.builder().book(book_1).author(author_1).build());
        entityManager.persist(BookAuthor.builder().book(book_1).author(author_2).build());
        entityManager.persist(BookAuthor.builder().book(book_1).author(author_3).build());

        entityManager.persist(BookInterest.builder().userId("LOGIN_USER_ID").book(book_1).build());
        entityManager.persist(BookInterest.builder().userId("LOGIN_USER_ID").book(book_2).build());

        IntStream.range(1, 21)
                .forEach(i -> {
                    MyBook myBook = entityManager.persist(MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookWithBook(book_1));

                    RecommendationFeed recommendationFeed = recommendationFeedRepository.save(RecommendationFeed.builder()
                            .userId("LOGIN_USER_ID_" + i)
                            .myBook(myBook)
                            .content("NEW_CONTENT_" + i)
                            .build());

                    entityManager.persist(RecommendationTarget.builder()
                            .recommendationFeed(recommendationFeed)
                            .targetName("TARGET_NAME_" + i)
                            .build());
                });

        entityManager.flush();
        entityManager.clear();

        // when
        List<RecommendationFeedViewAllModel> recommendationFeedViewAll =
                recommendationFeedRepository.getRecommendationFeedViewAll(null, 10, "LOGIN_USER_ID");

        // then
        assertAll(
                () -> assertThat(recommendationFeedViewAll).hasSize(10),
                () -> assertThat(recommendationFeedViewAll.get(0).getRecommendationTargets()).hasSize(1),
                () -> assertThat(recommendationFeedViewAll.get(0).getContent()).isEqualTo("NEW_CONTENT_20"),
                () -> assertThat(recommendationFeedViewAll.get(0).getRecommendationTargets()).extracting("targetName")
                        .containsExactly("TARGET_NAME_20"),
                () -> assertThat(recommendationFeedViewAll.get(0).getBookAuthors()).extracting("name")
                        .containsExactly("테스트 저자 1", "테스트 저자 2", "테스트 저자 3"),
                () -> assertThat(recommendationFeedViewAll.get(9).getRecommendationTargets()).hasSize(1),
                () -> assertThat(recommendationFeedViewAll.get(9).getContent()).isEqualTo("NEW_CONTENT_11"),
                () -> assertThat(recommendationFeedViewAll.get(9).getRecommendationTargets()).extracting("targetName")
                        .containsExactly("TARGET_NAME_11"),
                () -> assertThat(recommendationFeedViewAll.get(9).getBookAuthors()).extracting("name")
                        .containsExactly("테스트 저자 1", "테스트 저자 2", "테스트 저자 3"),
                () -> assertThat(recommendationFeedViewAll.get(0).getInterested()).isTrue(),
                () -> assertThat(recommendationFeedViewAll.get(9).getInterested()).isTrue()
        );
    }

    @DisplayName("1번째부터 10번째의 추천 피드를 조회한다. paging 처리")
    @Test
    void getRecommendationFeedViewOfUserModel() {

        // given
        Author author_1 = entityManager.persist(Author.builder().aid(11).name("테스트 저자 1").build());
        Author author_2 = entityManager.persist(Author.builder().aid(12).name("테스트 저자 2").build());
        Author author_3 = entityManager.persist(Author.builder().aid(13).name("테스트 저자 3").build());

        Book book = entityManager.persist(BookFixture.COMMON_BOOK_WITHOUT_RELATION.getBook());
        entityManager.persist(BookAuthor.builder().book(book).author(author_1).build());
        entityManager.persist(BookAuthor.builder().book(book).author(author_2).build());
        entityManager.persist(BookAuthor.builder().book(book).author(author_3).build());

        IntStream.range(1, 5)
                .forEach(i -> {
                    MyBook myBook = entityManager.persist(MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookWithBook(book));

                    RecommendationFeed recommendationFeed = recommendationFeedRepository.save(RecommendationFeed.builder()
                            .userId("LOGIN_USER_ID")
                            .myBook(myBook)
                            .content("NEW_CONTENT_" + i)
                            .build());

                    entityManager.persist(RecommendationTarget.builder()
                            .recommendationFeed(recommendationFeed)
                            .targetName("TARGET_NAME_" + i)
                            .build());
                });

        IntStream.range(5, 10)
                .forEach(i -> {
                    MyBook myBook = entityManager.persist(MyBookFixture.MY_BOOK_WITHOUT_RELATION.getMyBookWithBook(book));

                    RecommendationFeed recommendationFeed = recommendationFeedRepository.save(RecommendationFeed.builder()
                            .userId("LOGIN_USER_ID_" + i)
                            .myBook(myBook)
                            .content("NEW_CONTENT_" + i)
                            .build());

                    entityManager.persist(RecommendationTarget.builder()
                            .recommendationFeed(recommendationFeed)
                            .targetName("TARGET_NAME_" + i)
                            .build());
                });

        entityManager.flush();
        entityManager.clear();

        // when
        List<RecommendationFeedOfUserViewModel> recommendationFeedViewAll =
                recommendationFeedRepository.getRecommendationFeedViewOfUserModel("LOGIN_USER_ID");

        // then
        assertAll(
                () -> {
                    assertThat(recommendationFeedViewAll).hasSize(4);
                    assertThat(recommendationFeedViewAll.get(0).getRecommendationTargets()).extracting("targetName")
                            .containsExactly("TARGET_NAME_4");
                    assertThat(recommendationFeedViewAll.get(3).getRecommendationTargets()).extracting("targetName")
                            .containsExactly("TARGET_NAME_1");
                },
                () -> assertThat(recommendationFeedViewAll).extracting("content").containsExactlyInAnyOrder(
                        "NEW_CONTENT_1", "NEW_CONTENT_2", "NEW_CONTENT_3", "NEW_CONTENT_4"
                )
        );
    }
}