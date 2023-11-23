package kr.mybrary.bookservice.book.persistence;

import static kr.mybrary.bookservice.book.persistence.QBook.book;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BookRankedOrderType {

    NONE(new OrderSpecifier<>(Order.DESC, book.id)),
    HOLDER(new OrderSpecifier<>(Order.DESC, book.holderCount)),
    READ(new OrderSpecifier<>(Order.DESC, book.readCount)),
    INTEREST(new OrderSpecifier<>(Order.DESC, book.interestCount)),
    RECOMMENDATION(new OrderSpecifier<>(Order.DESC, book.recommendationFeedCount)),
    STAR(new OrderSpecifier<>(Order.DESC, book.starRating)),
    REVIEW(new OrderSpecifier<>(Order.DESC, book.reviewCount));

    private final OrderSpecifier<?> orderSpecifier;

    public OrderSpecifier<?> getOrderSpecifier() {
        return orderSpecifier;
    }

    public static BookRankedOrderType of(String orderType) {

        for (BookRankedOrderType value : values()) {
            if (value.name().equals(orderType.toUpperCase())) {
                return value;
            }
        }

        return NONE;
    }
}
