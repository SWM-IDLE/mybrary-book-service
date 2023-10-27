package kr.mybrary.bookservice.mybook.persistence.repository;

import static kr.mybrary.bookservice.book.persistence.QBook.book;
import static kr.mybrary.bookservice.book.persistence.bookInfo.QAuthor.author;
import static kr.mybrary.bookservice.book.persistence.bookInfo.QBookAuthor.bookAuthor;
import static kr.mybrary.bookservice.mybook.persistence.QMyBook.myBook;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kr.mybrary.bookservice.book.persistence.Book;
import kr.mybrary.bookservice.book.persistence.bookInfo.BookAuthor;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.mybook.persistence.MyBookOrderType;
import kr.mybrary.bookservice.mybook.persistence.ReadStatus;
import kr.mybrary.bookservice.mybook.persistence.model.MyBookListDisplayElementModel;
import kr.mybrary.bookservice.mybook.persistence.model.MyBookRegisteredListByDateModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MyBookRepositoryCustomImpl implements MyBookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MyBookListDisplayElementModel> findMyBookListDisplayElementModelsByUserId(String userId, MyBookOrderType myBookOrderType, ReadStatus readStatus) {
        List<MyBookListDisplayElementModel> myBookListDisplayElementModel = queryFactory
                .select(Projections.fields(MyBookListDisplayElementModel.class,
                        myBook.id.as("myBookId"),
                        myBook.showable.as("showable"),
                        myBook.exchangeable.as("exchangeable"),
                        myBook.shareable.as("shareable"),
                        myBook.readStatus.as("readStatus"),
                        myBook.startDateOfPossession.as("startDateOfPossession"),
                        myBook.book.id.as("bookId"),
                        myBook.book.title.as("title"),
                        myBook.book.description.as("description"),
                        myBook.book.thumbnailUrl.as("thumbnailUrl"),
                        myBook.book.starRating.as("starRating"),
                        myBook.book.publicationDate.as("publicationDate"))
                )
                .from(myBook)
                .where(myBook.userId.eq(userId),
                        eqReadStatus(readStatus))
                .orderBy(createOrderType(myBookOrderType))
                .fetch();

        for (MyBookListDisplayElementModel model : myBookListDisplayElementModel) {

            List<BookAuthor> fetch = queryFactory
                    .select(bookAuthor)
                    .from(bookAuthor)
                    .where(bookAuthor.book.id.eq(model.getBookId()))
                    .join(bookAuthor.author, author).fetchJoin()
                    .fetch();

            model.setBookAuthors(fetch);
        }

        return myBookListDisplayElementModel;
    }

    @Override
    public Long getBookRegistrationCountOfDay(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        return queryFactory.select(myBook.count())
                .from(myBook)
                .where(myBook.createdAt.between(start, end), myBook.showable.eq(true))
                .fetchOne();
    }

    @Override
    public List<String> getReadCompletedUserIdListByBook(Book book) {
        return queryFactory.select(myBook.userId)
                .from(myBook)
                .where(myBook.book.eq(book),
                        myBook.readStatus.eq(ReadStatus.COMPLETED),
                        myBook.showable.eq(true))
                .fetch();
    }

    @Override
    public List<String> getMyBookUserIdListByBook(Book book) {
        return queryFactory.select(myBook.userId)
                .from(myBook)
                .where(myBook.book.eq(book),
                        myBook.showable.eq(true))
                .fetch();
    }

    @Override
    public Optional<MyBook> getMyBookWithBookAndReviewUsingFetchJoin(Long mybookId) {
        return Optional.ofNullable(queryFactory.select(myBook)
                .from(myBook)
                .join(myBook.book).fetchJoin()
                .leftJoin(myBook.myReview).fetchJoin()
                .where(myBook.id.eq(mybookId))
                .fetchOne());
    }

    @Override
    public List<MyBookRegisteredListByDateModel> getMyBookRegisteredListBetweenDate(LocalDate start, LocalDate end) {
        return queryFactory.select(Projections.fields(MyBookRegisteredListByDateModel.class,
                    myBook.userId.as("userId"),
                    myBook.createdAt.as("registeredAt"),
                    myBook.book.title.as("title"),
                    myBook.book.isbn13.as("isbn13"),
                    myBook.book.thumbnailUrl.as("thumbnailUrl"))
                )
                .from(myBook)
                .join(myBook.book, book)
                .where(myBook.createdAt.between(start.atStartOfDay(), end.atTime(LocalTime.MAX)),
                        myBook.showable.eq(true))
                .orderBy(myBook.createdAt.desc())
                .fetch();
    }

    private BooleanExpression eqReadStatus(ReadStatus readStatus) {

        if (readStatus == null) {
            return null;
        }
        return myBook.readStatus.eq(readStatus);
    }

    private OrderSpecifier<?> createOrderType(MyBookOrderType myBookOrderType) {

        return Arrays.stream(MyBookOrderType.values())
                .filter(orderType -> orderType == myBookOrderType)
                .findFirst()
                .orElse(MyBookOrderType.NONE)
                .getOrderSpecifier();
    }

}
