package kr.mybrary.bookservice.book.persistence.repository;

import static kr.mybrary.bookservice.book.persistence.QBook.book;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.mybrary.bookservice.book.persistence.BookRankedOrderType;
import kr.mybrary.bookservice.book.persistence.model.RankedBookElementModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RankedBookElementModel> findRankedBookListBy(int limit, BookRankedOrderType bookRankedOrderType) {

        return queryFactory
                .select(Projections.fields(RankedBookElementModel.class,
                        book.title.as("title"),
                        book.thumbnailUrl.as("thumbnailUrl"),
                        book.isbn13.as("isbn13"),
                        book.starRating.as("starRating"),
                        book.holderCount.as("holderCount"),
                        book.readCount.as("readCount"),
                        book.interestCount.as("interestCount"),
                        book.recommendationFeedCount.as("recommendationFeedCount"),
                        book.reviewCount.as("reviewCount")))
                .from(book)
                .orderBy(bookRankedOrderType.getOrderSpecifier())
                .limit(limit)
                .fetch();
    }
}
