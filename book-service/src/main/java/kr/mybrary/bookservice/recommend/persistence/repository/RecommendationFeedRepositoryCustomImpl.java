package kr.mybrary.bookservice.recommend.persistence.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.querydsl.core.types.Projections.fields;
import static kr.mybrary.bookservice.book.persistence.QBook.book;
import static kr.mybrary.bookservice.book.persistence.bookInfo.QAuthor.author;
import static kr.mybrary.bookservice.book.persistence.bookInfo.QBookAuthor.bookAuthor;
import static kr.mybrary.bookservice.mybook.persistence.QMyBook.myBook;
import static kr.mybrary.bookservice.recommend.persistence.QRecommendationFeed.recommendationFeed;
import static kr.mybrary.bookservice.recommend.persistence.QRecommendationTarget.recommendationTarget;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
    public List<RecommendationFeedViewAllModel> getRecommendationFeedViewAll() {
        return queryFactory.selectFrom(recommendationFeed)
                .join(recommendationFeed.myBook, myBook).on(recommendationFeed.myBook.id.eq(myBook.id))
                .join(recommendationFeed.myBook.book, book).on(recommendationFeed.myBook.book.id.eq(book.id))
                .leftJoin(recommendationTarget).on(recommendationFeed.id.eq(recommendationTarget.recommendationFeed.id))
                .leftJoin(bookAuthor).on(bookAuthor.book.id.eq(book.id))
                .join(author).on(bookAuthor.author.id.eq(author.id))
                .transform(groupBy(recommendationFeed.id).list(fields(RecommendationFeedViewAllModel.class,
                        recommendationFeed.content.as("content"),
                        recommendationFeed.userId.as("userId"),
                        myBook.id.as("myBookId"),
                        book.id.as("bookId"),
                        book.title.as("title"),
                        book.isbn13.as("isbn13"),
                        book.thumbnailUrl.as("thumbnailUrl"),
                        book.holderCount.as("holderCount"),
                        book.interestCount.as("interestCount"),
                        set(
                                fields(RecommendationTargetModel.class,
                                        recommendationTarget.id.as("targetId"),
                                        recommendationTarget.targetName.as("targetName")
                                )
                        ).as("recommendationTargets"),
                        set(
                                fields(BookAuthorModel.class,
                                        author.id.as("authorId"),
                                        author.aid.as("aid"),
                                        author.name.as("name")
                                )
                        ).as("bookAuthors")
                )));
    }


}
