package kr.mybrary.bookservice.recommend.persistence.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.fields;
import static com.querydsl.core.types.dsl.Expressions.set;
import static kr.mybrary.bookservice.book.persistence.QBook.book;
import static kr.mybrary.bookservice.book.persistence.QBookInterest.bookInterest;
import static kr.mybrary.bookservice.book.persistence.bookInfo.QAuthor.author;
import static kr.mybrary.bookservice.book.persistence.bookInfo.QBookAuthor.bookAuthor;
import static kr.mybrary.bookservice.mybook.persistence.QMyBook.myBook;
import static kr.mybrary.bookservice.recommend.persistence.QRecommendationFeed.recommendationFeed;
import static kr.mybrary.bookservice.recommend.persistence.QRecommendationTarget.recommendationTarget;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfUserViewModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedOfUserViewModel.RecommendationTargetOfUserModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel.BookAuthorModel;
import kr.mybrary.bookservice.recommend.persistence.model.RecommendationFeedViewAllModel.RecommendationTargetModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecommendationFeedRepositoryCustomImpl implements RecommendationFeedRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RecommendationFeedViewAllModel> getRecommendationFeedViewAll(Long recommendationFeedId, int pageSize, String userId) {
        List<RecommendationFeedViewAllModel> models = queryFactory.select(fields(RecommendationFeedViewAllModel.class,
                        recommendationFeed.id.as("recommendationFeedId"),
                        recommendationFeed.content.as("content"),
                        recommendationFeed.userId.as("userId"),
                        myBook.id.as("myBookId"),
                        book.id.as("bookId"),
                        book.title.as("title"),
                        book.isbn13.as("isbn13"),
                        book.thumbnailUrl.as("thumbnailUrl"),
                        book.holderCount.as("holderCount"),
                        book.interestCount.as("interestCount")
                ))
                .from(recommendationFeed)
                .where(ltRecommendationFeedId(recommendationFeedId))
                .orderBy(recommendationFeed.id.desc())
                .limit(pageSize)
                .join(recommendationFeed.myBook, myBook).on(recommendationFeed.myBook.id.eq(myBook.id))
                .join(recommendationFeed.myBook.book, book).on(recommendationFeed.myBook.book.id.eq(book.id))
                .fetch();

        List<Long> recommendationFeedIds = models.stream()
                .map(RecommendationFeedViewAllModel::getRecommendationFeedId)
                .toList();

        List<Long> bookIds = models.stream()
                .map(RecommendationFeedViewAllModel::getBookId)
                .toList();

        Map<Long, List<RecommendationTargetModel>> recommendationTargetModelMap = queryFactory
                .select(
                        fields(RecommendationTargetModel.class,
                                recommendationTarget.id.as("targetId"),
                                recommendationTarget.targetName.as("targetName")
                        )
                ).from(recommendationTarget)
                .where(recommendationTarget.recommendationFeed.id.in(recommendationFeedIds))
                .transform(groupBy(recommendationTarget.recommendationFeed.id)
                        .as(list(fields(RecommendationTargetModel.class,
                                recommendationTarget.id.as("targetId"),
                                recommendationTarget.targetName.as("targetName")
                        ))));

        Map<Long, List<BookAuthorModel>> bookAuthorModelMap = queryFactory
                .select(
                        fields(BookAuthorModel.class,
                                author.id.as("authorId"),
                                author.aid.as("aid"),
                                author.name.as("name")
                        )
                ).from(bookAuthor)
                .join(bookAuthor.author, author).on(bookAuthor.author.id.eq(author.id))
                .where(bookAuthor.book.id.in(bookIds))
                .transform(groupBy(bookAuthor.book.id)
                        .as(list(fields(BookAuthorModel.class,
                                author.id.as("authorId"),
                                author.aid.as("aid"),
                                author.name.as("name")
                        ))));

        Set<Long> interestedBookIdSet = queryFactory.select(book.id).from(bookInterest)
                .where(bookInterest.userId.eq(userId))
                .transform(groupBy(book.id).as(set(book.id))).keySet();

        models.forEach(model -> {
            model.setRecommendationTargets(recommendationTargetModelMap.getOrDefault(model.getRecommendationFeedId(), List.of()));
            model.setBookAuthors(bookAuthorModelMap.getOrDefault(model.getBookId(), List.of()));
            model.setInterested(interestedBookIdSet.contains(model.getBookId()));
        });

        return models;
    }

    @Override
    public Optional<RecommendationFeed> getRecommendationFeedWithTargets(Long recommendationFeedId) {
        return Optional.ofNullable(queryFactory.selectFrom(recommendationFeed)
                .where(recommendationFeed.id.eq(recommendationFeedId))
                .leftJoin(recommendationFeed.recommendationTargets.feedRecommendationTargets, recommendationTarget).fetchJoin()
                .fetchOne());
    }

    private BooleanExpression ltRecommendationFeedId(Long recommendationFeedId) {
        if (recommendationFeedId == null) {
            return null;
        }

        return recommendationFeed.id.lt(recommendationFeedId);
    }

    @Override
    public List<RecommendationFeedOfUserViewModel> getRecommendationFeedViewOfUserModel(String userId) {
        List<RecommendationFeedOfUserViewModel> models = queryFactory.select(fields(RecommendationFeedOfUserViewModel.class,
                        recommendationFeed.id.as("recommendationFeedId"),
                        recommendationFeed.content.as("content"),
                        recommendationFeed.userId.as("userId"),
                        recommendationFeed.createdAt.as("createdAt"),
                        myBook.id.as("myBookId"),
                        book.id.as("bookId"),
                        book.title.as("title"),
                        book.isbn13.as("isbn13"),
                        book.thumbnailUrl.as("thumbnailUrl")
                ))
                .from(recommendationFeed)
                .where(recommendationFeed.userId.eq(userId))
                .orderBy(recommendationFeed.id.desc())
                .join(recommendationFeed.myBook, myBook).on(recommendationFeed.myBook.id.eq(myBook.id))
                .join(recommendationFeed.myBook.book, book).on(recommendationFeed.myBook.book.id.eq(book.id))
                .fetch();

        List<Long> recommendationFeedIds = models.stream()
                .map(RecommendationFeedOfUserViewModel::getRecommendationFeedId)
                .toList();

        Map<Long, List<RecommendationTargetOfUserModel>> recommendationTargetModelMap = queryFactory
                .select(
                        fields(RecommendationTargetOfUserModel.class,
                                recommendationTarget.id.as("targetId"),
                                recommendationTarget.targetName.as("targetName")
                        )
                ).from(recommendationTarget)
                .where(recommendationTarget.recommendationFeed.id.in(recommendationFeedIds))
                .transform(groupBy(recommendationTarget.recommendationFeed.id)
                        .as(list(fields(RecommendationTargetOfUserModel.class,
                                recommendationTarget.id.as("targetId"),
                                recommendationTarget.targetName.as("targetName")
                        ))));

        models.forEach(model -> {
            model.setRecommendationTargets(recommendationTargetModelMap.getOrDefault(model.getRecommendationFeedId(), List.of()));
        });

        return models;
    }
}
