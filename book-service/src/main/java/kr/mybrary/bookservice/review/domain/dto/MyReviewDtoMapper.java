package kr.mybrary.bookservice.review.domain.dto;

import java.time.LocalDateTime;
import java.util.List;
import kr.mybrary.bookservice.global.util.DateUtils;
import kr.mybrary.bookservice.review.persistence.model.MyReviewElementByUserIdModel;
import kr.mybrary.bookservice.review.persistence.model.MyReviewElementByUserIdModel.BookAuthorModel;
import kr.mybrary.bookservice.review.persistence.model.MyReviewFromMyBookModel;
import kr.mybrary.bookservice.review.presentation.dto.response.MyReviewOfMyBookGetResponse;
import kr.mybrary.bookservice.review.presentation.dto.response.MyReviewOfUserIdGetResponse.MyReviewOfUserIdElement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MyReviewDtoMapper {


    MyReviewDtoMapper INSTANCE = Mappers.getMapper(MyReviewDtoMapper.class);


    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "toFormatMyBookReviewUI")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "toFormatMyBookReviewUI")
    MyReviewOfMyBookGetResponse reviewOfMyBookModelToResponse(MyReviewFromMyBookModel reviewOfMyBookGetModel);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "toFormatMyBookReviewUI")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "toFormatMyBookReviewUI")
    @Mapping(target = "authors", source = "bookAuthors", qualifiedByName = "getAuthors")
    MyReviewOfUserIdElement myReviewByUserIdModelToElement(MyReviewElementByUserIdModel myReviewElementByUserIdModel);

    @Named("toFormatMyBookReviewUI")
    static String toFormatMyBookReviewUI(LocalDateTime dateTime) {
        return DateUtils.toDotFormatYYYYMMDD(dateTime);
    }

    @Named("getAuthors")
    static List<String> getAuthors(List<MyReviewElementByUserIdModel.BookAuthorModel> bookAuthors) {
        return bookAuthors.stream()
                .map(BookAuthorModel::getName)
                .toList();
    }
}
