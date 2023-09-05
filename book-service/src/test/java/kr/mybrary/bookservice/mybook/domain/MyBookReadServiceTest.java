package kr.mybrary.bookservice.mybook.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.mybrary.bookservice.book.BookFixture;
import kr.mybrary.bookservice.book.domain.BookReadService;
import kr.mybrary.bookservice.book.persistence.Book;
import kr.mybrary.bookservice.client.user.api.UserServiceClient;
import kr.mybrary.bookservice.mybook.MyBookFixture;
import kr.mybrary.bookservice.mybook.MybookDtoTestData;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookDetailServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookFindAllServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookFindByMeaningTagQuoteServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookReadCompletedStatusServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookRegisteredStatusServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.UserInfoWithMyBookSetForBookServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.UserInfoWithReadCompletedForBookServiceRequest;
import kr.mybrary.bookservice.mybook.domain.exception.MyBookAccessDeniedException;
import kr.mybrary.bookservice.mybook.domain.exception.MyBookNotFoundException;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.mybook.persistence.ReadStatus;
import kr.mybrary.bookservice.mybook.persistence.repository.MyBookRepository;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookDetailResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookElementFromMeaningTagResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookElementResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookReadCompletedStatusResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookRegisteredStatusResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookRegistrationCountResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.UserInfoWithMyBookSetForBookResponse;
import kr.mybrary.bookservice.mybook.presentation.dto.response.UserInfoWithReadCompletedForBookResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MyBookReadServiceTest {

    @InjectMocks
    private MyBookReadService myBookReadService;

    @Mock
    private MyBookRepository myBookRepository;

    @Mock
    private BookReadService bookReadService;

    @Mock
    private UserServiceClient userServiceClient;

    private static final String LOGIN_ID = "LOGIN_USER_ID";
    private static final String OTHER_USER_ID = "OTHER_USER_ID";

    @DisplayName("나의 마이북을 모두 조회시, 비공개 설정된 마이북도 모두 조회한다.")
    @Test
    void findAllMyBooks() {

        //given
        MyBookFindAllServiceRequest request = MybookDtoTestData.createMyBookFindAllServiceRequest(LOGIN_ID, LOGIN_ID);

        given(myBookRepository.findMyBookListDisplayElementModelsByUserId(any(), any(), any())).willReturn(
                List.of(MybookDtoTestData.createMyBookListDisplayElementModelBuilder().build(),
                        MybookDtoTestData.createMyBookListDisplayElementModelBuilder().build()));

        // when,
        List<MyBookElementResponse> response = myBookReadService.findAllMyBooks(request);

        // then
        assertAll(
                () -> assertThat(response.size()).isEqualTo(2),
                () -> verify(myBookRepository).findMyBookListDisplayElementModelsByUserId(any(), any(), any())
        );
    }

    @DisplayName("다른 유저의 마이북을 모두 조회시, 비공개 설정된 마이북은 조회되지 않는다.")
    @Test
    void findOtherUserAllMyBooks() {

        //given
        MyBookFindAllServiceRequest request = MybookDtoTestData.createMyBookFindAllServiceRequest(OTHER_USER_ID, LOGIN_ID);

        given(myBookRepository.findMyBookListDisplayElementModelsByUserId(any(), any(), any())).willReturn(
                List.of(MybookDtoTestData.createMyBookListDisplayElementModelBuilder().build(),
                        MybookDtoTestData.createMyBookListDisplayElementModelBuilder().showable(false).build()));

        // when
        List<MyBookElementResponse> response = myBookReadService.findAllMyBooks(request);

        // then
        assertAll(
                () -> assertThat(response.size()).isEqualTo(1),
                () -> verify(myBookRepository).findMyBookListDisplayElementModelsByUserId(any(), any(), any())
        );
    }

    @DisplayName("내 마이북 상세보기한다.")
    @Test
    void findMyBookDetail() {

        //given
        MyBookDetailServiceRequest request = MyBookDetailServiceRequest.of(LOGIN_ID, 1L);
        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook();

        given(myBookRepository.findMyBookDetailUsingFetchJoin(any())).willReturn(Optional.ofNullable(myBook));

        // when
        MyBookDetailResponse myBookDetail = myBookReadService.findMyBookDetail(request);

        // then
        assertAll(
                () -> assertThat(myBookDetail).isInstanceOf(MyBookDetailResponse.class),
                () -> {
                    assertNotNull(myBook);
                    assertThat(myBookDetail.getId()).isEqualTo(myBook.getId());
                },
                () -> verify(myBookRepository).findMyBookDetailUsingFetchJoin(request.getMybookId())
        );
    }

    @DisplayName("마이북 ID에 해당하는 마이북이 없으면 예외가 발생한다.")
    @Test
    void occurExceptionWhenFindMyBookDetailWithNotExistMyBook() {

        //given
        MyBookDetailServiceRequest request = MyBookDetailServiceRequest.of(LOGIN_ID, 1L);

        given(myBookRepository.findMyBookDetailUsingFetchJoin(any())).willReturn(Optional.empty());

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> myBookReadService.findMyBookDetail(request))
                        .isInstanceOf(MyBookNotFoundException.class),
                () -> verify(myBookRepository).findMyBookDetailUsingFetchJoin(request.getMybookId())
        );
    }

    @DisplayName("다른 사람의 비공개 마이북 상세보기를 요청하면 예외가 발생한다.")
    @Test
    void occurExceptionWhenFindOtherUserPrivateMyBookDetail() {

        //given
        MyBookDetailServiceRequest request = MyBookDetailServiceRequest.of(LOGIN_ID, 1L);
        MyBook myBook = MyBookFixture.NOT_SHOWABLE_OTHER_USER_MYBOOK.getMyBook();

        given(myBookRepository.findMyBookDetailUsingFetchJoin(any())).willReturn(Optional.ofNullable(myBook));

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> myBookReadService.findMyBookDetail(request))
                        .isInstanceOf(MyBookAccessDeniedException.class),
                () -> verify(myBookRepository).findMyBookDetailUsingFetchJoin(request.getMybookId())
        );
    }


    @DisplayName("의미 태그 문구를 통해서 마이북을 조회한다.")
    @Test
    void findMyBookByMeaningTagQuote() {

        // given
        MyBookFindByMeaningTagQuoteServiceRequest request = MyBookFindByMeaningTagQuoteServiceRequest.of(LOGIN_ID,
                "quote");

        given(myBookRepository.findByMeaningTagQuote(request.getQuote())).willReturn(
                List.of(MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook()));

        // when
        List<MyBookElementFromMeaningTagResponse> result = myBookReadService.findByMeaningTagQuote(request);

        // then
        assertAll(
                () -> verify(myBookRepository, times(1)).findByMeaningTagQuote(request.getQuote()),
                () -> assertThat(result.size()).isEqualTo(1)
        );
    }

    @DisplayName("의미 태그 문구를 통해서 마이북을 조회시, 다른 유저의 비공개 마이북은 조회되지 않는다.")
    @Test
    void findMyBookByMeaningTagQuoteAndNotShowable() {

        // given
        MyBookFindByMeaningTagQuoteServiceRequest request = MyBookFindByMeaningTagQuoteServiceRequest.of(LOGIN_ID,
                "quote");

        given(myBookRepository.findByMeaningTagQuote(request.getQuote())).willReturn(
                List.of(MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook(),
                        MyBookFixture.NOT_SHOWABLE_LOGIN_USER_MYBOOK.getMyBook(),
                        MyBookFixture.NOT_SHOWABLE_OTHER_USER_MYBOOK.getMyBook(),
                        MyBookFixture.NOT_SHOWABLE_OTHER_USER_MYBOOK.getMyBook()));

        // when
        List<MyBookElementFromMeaningTagResponse> result = myBookReadService.findByMeaningTagQuote(request);

        // then
        assertAll(
                () -> verify(myBookRepository, times(1)).findByMeaningTagQuote(request.getQuote()),
                () -> assertThat(result.size()).isEqualTo(2)
        );
    }

    @DisplayName("마이북 Id를 통해 마이북과 마이북의 도서를 조회한다.")
    @Test
    void findMyBookByIdWithBook() {

        // given
        Long myBookId = 1L;
        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook();

        given(myBookRepository.findByIdWithBook(myBookId)).willReturn(Optional.ofNullable(myBook));

        // when
        MyBook foundMyBook = myBookReadService.findMyBookByIdWithBook(myBookId);

        // then
        assertAll(
                () -> verify(myBookRepository, times(1)).findByIdWithBook(myBookId),
                () -> assertThat(foundMyBook).isNotNull()
        );
    }

    @DisplayName("오늘 등록된 마이북의 갯수를 조회한다.")
    @Test
    void getBookRegistrationCountOfToday() {

        // given
        given(myBookRepository.getBookRegistrationCountOfDay(LocalDate.now())).willReturn(1L);

        // when
        MyBookRegistrationCountResponse response = myBookReadService.getBookRegistrationCountOfToday();

        // then
        assertAll(
                () -> verify(myBookRepository, times(1)).getBookRegistrationCountOfDay(any()),
                () -> assertThat(response.getCount()).isEqualTo(1L)
        );
    }

    @DisplayName("로그인 유저가 해당 도서를 마이북으로 등록했는지 확인한다.")
    @Test
    void isLoginUserRegisterThisBookAsMyBook() {

        // given
        MyBookRegisteredStatusServiceRequest request = MybookDtoTestData.createMyBookRegisteredStatusServiceRequest();
        Book book = BookFixture.COMMON_BOOK.getBook();

        given(bookReadService.findOptionalBookByISBN13(request.getIsbn13())).willReturn(Optional.of(book));
        given(myBookRepository.existsByUserIdAndBook(request.getLoginId(), book)).willReturn(true);

        // when
        MyBookRegisteredStatusResponse response = myBookReadService.getMyBookRegisteredStatus(request);

        // then
        assertAll(
                () -> verify(bookReadService, times(1)).findOptionalBookByISBN13(anyString()),
                () -> verify(myBookRepository, times(1)).existsByUserIdAndBook(anyString(), any()),
                () -> assertThat(response.isRegistered()).isTrue()
        );
    }

    @DisplayName("로그인 유저가 해당 도서를 마이북으로 등록했는지 확인시, 도서가 존재하지 않으면 false를 반환한다.")
    @Test
    void returnFalseResponseWhenBookIsNotExisted() {

        // given
        MyBookRegisteredStatusServiceRequest request = MybookDtoTestData.createMyBookRegisteredStatusServiceRequest();

        given(bookReadService.findOptionalBookByISBN13(request.getIsbn13())).willReturn(Optional.empty());

        // when
        MyBookRegisteredStatusResponse response = myBookReadService.getMyBookRegisteredStatus(request);

        // then
        assertAll(
                () -> verify(bookReadService, times(1)).findOptionalBookByISBN13(anyString()),
                () -> verify(myBookRepository, never()).existsByUserIdAndBook(anyString(), any()),
                () -> assertThat(response.isRegistered()).isFalse()
        );
    }

    @DisplayName("로그인 유저가 해당 도서를 완독했는지 확인한다.")
    @Test
    void isLoginUserReadCompleteThisBook() {

        // given
        MyBookReadCompletedStatusServiceRequest request = MybookDtoTestData.createMyBookReadCompletedStatusServiceRequest();
        Book book = BookFixture.COMMON_BOOK.getBook();
        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBookBuilder()
                .readStatus(ReadStatus.COMPLETED).build();

        given(bookReadService.findOptionalBookByISBN13(request.getIsbn13())).willReturn(Optional.of(book));
        given(myBookRepository.findByUserIdAndBook(request.getLoginId(), book)).willReturn(Optional.of(myBook));

        // when
        MyBookReadCompletedStatusResponse response = myBookReadService.getMyBookReadCompletedStatus(request);

        // then
        assertAll(
                () -> verify(bookReadService, times(1)).findOptionalBookByISBN13(anyString()),
                () -> verify(myBookRepository, times(1)).findByUserIdAndBook(any(), any()),
                () -> assertThat(response.isCompleted()).isTrue()
        );
    }

    @DisplayName("로그인 유저가 해당 도서를 완독했는지 확인시, 도서가 존재하지 않으면 false를 반환한다.")
    @Test
    void returnReadStatusFalseResponseWhenBookIsNotExisted() {

        // given
        MyBookReadCompletedStatusServiceRequest request = MybookDtoTestData.createMyBookReadCompletedStatusServiceRequest();
        given(bookReadService.findOptionalBookByISBN13(request.getIsbn13())).willReturn(Optional.empty());

        // when
        MyBookReadCompletedStatusResponse response = myBookReadService.getMyBookReadCompletedStatus(request);

        // then
        assertAll(
                () -> verify(bookReadService, times(1)).findOptionalBookByISBN13(anyString()),
                () -> verify(myBookRepository, never()).findByUserIdAndBook(any(), any()),
                () -> assertThat(response.isCompleted()).isFalse()
        );
    }

    @DisplayName("로그인 유저가 해당 도서를 완독했는지 확인시, 마이북으로 등록되어 있지 않으면 false를 반환한다.")
    @Test
    void returnReadStatusFalseResponseWhenMyBookIsNotRegistered() {

        // given
        MyBookReadCompletedStatusServiceRequest request = MybookDtoTestData.createMyBookReadCompletedStatusServiceRequest();
        Book book = BookFixture.COMMON_BOOK.getBook();
        String userId = "LOGIN_USER_ID";

        given(bookReadService.findOptionalBookByISBN13(request.getIsbn13())).willReturn(Optional.of(book));
        given(myBookRepository.findByUserIdAndBook(userId, book)).willReturn(Optional.empty());

        // when
        MyBookReadCompletedStatusResponse response = myBookReadService.getMyBookReadCompletedStatus(request);

        // then
        assertAll(
                () -> verify(bookReadService, times(1)).findOptionalBookByISBN13(anyString()),
                () -> verify(myBookRepository, times(1)).findByUserIdAndBook(any(), any()),
                () -> assertThat(response.isCompleted()).isFalse()
        );
    }

    @DisplayName("한 도서에 대해서 완독한 유저들의 정보를 조회한다.")
    @Test
    void getReadCompletedUserIdListByBook() {

        // given
        UserInfoWithReadCompletedForBookServiceRequest request = MybookDtoTestData.createUserInfoWithReadCompletedForBookServiceRequest();
        Book book = BookFixture.COMMON_BOOK.getBook();
        List<String> userIds = List.of("user1", "user2");

        given(bookReadService.getRegisteredBookByISBN13(request.getIsbn13())).willReturn(book);
        given(myBookRepository.getReadCompletedUserIdListByBook(book)).willReturn(userIds);
        given(userServiceClient.getUsersInfo(userIds)).willReturn(MybookDtoTestData.createUserInfoResponseList(userIds));

        // when
        UserInfoWithReadCompletedForBookResponse response = myBookReadService.getUserIdWithReadCompletedListByBook(request);

        // then
        assertAll(
                () -> assertThat(response.getUserInfos().size()).isEqualTo(2),
                () -> assertThat(response.getUserInfos()).extracting("userId").containsExactlyInAnyOrder("user1", "user2"),
                () -> verify(bookReadService, times(1)).getRegisteredBookByISBN13(anyString()),
                () -> verify(myBookRepository, times(1)).getReadCompletedUserIdListByBook(any()),
                () -> verify(userServiceClient, times(1)).getUsersInfo(any())
        );
    }

    @DisplayName("한 도서에 대해서 마이북으로 설정한 유저들의 정보를 조회한다.")
    @Test
    void getMyBookSetUserIdListByBook() {

        // given
        UserInfoWithMyBookSetForBookServiceRequest request = MybookDtoTestData.createUserInfoWithMyBookSetForBookServiceRequest();
        Book book = BookFixture.COMMON_BOOK.getBook();
        List<String> userIds = List.of("user1", "user2");

        given(bookReadService.getRegisteredBookByISBN13(request.getIsbn13())).willReturn(book);
        given(myBookRepository.getMyBookUserIdListByBook(book)).willReturn(userIds);
        given(userServiceClient.getUsersInfo(userIds)).willReturn(MybookDtoTestData.createUserInfoResponseList(userIds));

        // when
        UserInfoWithMyBookSetForBookResponse response = myBookReadService.getUserIdListWithMyBookSettingByBook(request);

        // then
        assertAll(
                () -> assertThat(response.getUserInfos().size()).isEqualTo(2),
                () -> assertThat(response.getUserInfos()).extracting("userId").containsExactlyInAnyOrder("user1", "user2"),
                () -> verify(bookReadService, times(1)).getRegisteredBookByISBN13(anyString()),
                () -> verify(myBookRepository, times(1)).getMyBookUserIdListByBook(any()),
                () -> verify(userServiceClient, times(1)).getUsersInfo(any())
        );
    }
}