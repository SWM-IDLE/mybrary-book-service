package kr.mybrary.bookservice.recommend.persistence.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.fields;
import static kr.mybrary.bookservice.book.persistence.QBook.book;
import static kr.mybrary.bookservice.book.persistence.bookInfo.QAuthor.author;
import static kr.mybrary.bookservice.book.persistence.bookInfo.QBookAuthor.bookAuthor;
import static kr.mybrary.bookservice.mybook.persistence.QMyBook.myBook;
import static kr.mybrary.bookservice.recommend.persistence.QRecommendationFeed.recommendationFeed;
import static kr.mybrary.bookservice.recommend.persistence.QRecommendationTarget.recommendationTarget;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
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
    public List<RecommendationFeedViewAllModel> getRecommendationFeedViewAll(Long RecommendationFeedId, int pageSize) {
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
                .where(ltRecommendationFeedId(RecommendationFeedId))
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

        models.forEach(model -> {
            model.setRecommendationTargets(recommendationTargetModelMap.getOrDefault(model.getRecommendationFeedId(), List.of()));
            model.setBookAuthors(bookAuthorModelMap.getOrDefault(model.getBookId(), List.of()));
        });

        return models;
    }


    private BooleanExpression ltRecommendationFeedId(Long recommendationFeedId) {
        if (recommendationFeedId == null) {
            return null;
        }

        return recommendationFeed.id.lt(recommendationFeedId);
    }
}
