package kr.mybrary.bookservice.mybook.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import kr.mybrary.bookservice.book.BookFixture;
import kr.mybrary.bookservice.book.domain.BookReadService;
import kr.mybrary.bookservice.book.persistence.Book;
import kr.mybrary.bookservice.mybook.MyBookFixture;
import kr.mybrary.bookservice.mybook.MybookDtoTestData;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookCreateServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MyBookDeleteServiceRequest;
import kr.mybrary.bookservice.mybook.domain.dto.request.MybookUpdateServiceRequest;
import kr.mybrary.bookservice.mybook.domain.exception.MyBookAccessDeniedException;
import kr.mybrary.bookservice.mybook.domain.exception.MyBookAlreadyExistsException;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.mybook.persistence.ReadStatus;
import kr.mybrary.bookservice.mybook.persistence.repository.MyBookRepository;
import kr.mybrary.bookservice.mybook.presentation.dto.response.MyBookUpdateResponse;
import kr.mybrary.bookservice.tag.domain.MeaningTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MyBookWriteServiceTest {

    @InjectMocks
    private MyBookWriteService myBookWriteService;

    @Mock
    private MyBookRepository myBookRepository;

    @Mock
    private BookReadService bookReadService;

    @Mock
    private MeaningTagService meaningTagService;

    private static final String LOGIN_ID = "LOGIN_USER_ID";
    private static final Long MYBOOK_ID = 1L;


    @DisplayName("도서를 마이북으로 등록한다.")
    @Test
    void registerMyBook() {

        // given
        MyBookCreateServiceRequest request = MybookDtoTestData.createMyBookCreateServiceRequest();
        Book foundBook = BookFixture.COMMON_BOOK.getBook();
        int foundBookHolderCount = foundBook.getHolderCount();

        given(bookReadService.getRegisteredBookByISBN13(anyString())).willReturn(foundBook);
        given(myBookRepository.existsByUserIdAndBook(any(), any())).willReturn(false);
        given(myBookRepository.save(any())).willReturn(any());

        // when
        myBookWriteService.create(request);

        // then
        assertAll(
                () -> verify(bookReadService, times(1)).getRegisteredBookByISBN13(anyString()),
                () -> verify(myBookRepository, times(1)).existsByUserIdAndBook(any(), any()),
                () -> verify(myBookRepository, times(1)).save(any()),
                () -> assertThat(foundBook.getHolderCount()).isEqualTo(foundBookHolderCount + 1)
        );
    }

    @DisplayName("기존에 마이북으로 설정한 도서를 마이북으로 등록하면 예외가 발생한다.")
    @Test
    void occurExceptionWhenRegisterDuplicatedBook() {

        // given
        MyBookCreateServiceRequest request = MybookDtoTestData.createMyBookCreateServiceRequest();

        given(bookReadService.getRegisteredBookByISBN13(anyString())).willReturn(Book.builder().id(1L).build());
        given(myBookRepository.existsByUserIdAndBook(any(), any())).willReturn(true);

        // when, then
        assertThrows(MyBookAlreadyExistsException.class, () -> myBookWriteService.create(request));

        assertAll(
                () -> verify(bookReadService, times(1)).getRegisteredBookByISBN13(anyString()),
                () -> verify(myBookRepository, times(1)).existsByUserIdAndBook(any(), any()),
                () -> verify(myBookRepository, never()).save(any())
        );
    }

    @DisplayName("마이북을 삭제한다.")
    @Test
    void deleteMyBook() {

        //given
        MyBookDeleteServiceRequest request = MyBookDeleteServiceRequest.of(LOGIN_ID, 1L);
        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook();

        int holderCount = myBook.getBook().getHolderCount();

        given(myBookRepository.findById(any())).willReturn(Optional.of(myBook));
        doNothing().when(myBookRepository).delete(any());

        // when
        myBookWriteService.deleteMyBook(request);

        // then
        assertAll(
                () -> verify(myBookRepository).findById(request.getMybookId()),
                () -> verify(myBookRepository).delete(myBook),
                () -> assertThat(myBook).isNotNull(),
                () -> assertThat(myBook.getBook().getHolderCount()).isEqualTo(holderCount - 1)
        );
    }

    @DisplayName("다른 유저의 마이북을 삭제시, 예외가 발생한다.")
    @Test
    void occurExceptionWhenDeleteOtherUserMyBook() {

        //given
        MyBookDeleteServiceRequest request = MyBookDeleteServiceRequest.of(LOGIN_ID, 1L);
        MyBook myBook = MyBookFixture.COMMON_OTHER_USER_MYBOOK.getMyBook();

        given(myBookRepository.findById(any())).willReturn(Optional.ofNullable(myBook));

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> myBookWriteService.deleteMyBook(request))
                        .isInstanceOf(MyBookAccessDeniedException.class),
                () -> {
                    assertThat(myBook).isNotNull();
                    assertThat(myBook.isDeleted()).isFalse();
                },
                () -> verify(myBookRepository).findById(request.getMybookId()),
                () -> verify(myBookRepository, never()).delete(any())
        );
    }

    @DisplayName("마이북을 수정한다.")
    @Test
    void updateMyBook() {

        //given
        MybookUpdateServiceRequest request = MybookDtoTestData.createMyBookUpdateServiceRequest(LOGIN_ID, MYBOOK_ID).build();
        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook();

        given(myBookRepository.findById(any())).willReturn(Optional.ofNullable(myBook));
        willDoNothing().given(meaningTagService).assignMeaningTag(any());

        // when
        MyBookUpdateResponse response = myBookWriteService.updateMyBookProperties(request);

        // then
        assertAll(
                () -> verify(myBookRepository).findById(request.getMyBookId()),
                () -> {
                    assertThat(myBook).isNotNull();
                    assertThat(response.getStartDateOfPossession()).isEqualTo(request.getStartDateOfPossession());
                    assertThat(response.isExchangeable()).isEqualTo(request.isExchangeable());
                    assertThat(response.isShareable()).isEqualTo(request.isShareable());
                    assertThat(response.isShowable()).isEqualTo(request.isShowable());
                    assertThat(response.getReadStatus()).isEqualTo(request.getReadStatus());
                    assertThat(response.getMeaningTag().getQuote()).isEqualTo(request.getMeaningTag().getQuote());
                    assertThat(response.getMeaningTag().getColorCode()).isEqualTo(request.getMeaningTag().getColorCode());
                }
        );
    }

    @DisplayName("독서 상태를 완독으로 수정할 때, 도서의 readCount가 1 증가한다.")
    @Test
    void updateMyBookReadStatusToCompleted() {

        //given
        MybookUpdateServiceRequest request = MybookDtoTestData.createMyBookUpdateServiceRequest(LOGIN_ID, MYBOOK_ID)
                .readStatus(ReadStatus.COMPLETED)
                .build();

        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook();
        Integer originReadCount = myBook.getBook().getReadCount();

        given(myBookRepository.findById(any())).willReturn(Optional.of(myBook));
        willDoNothing().given(meaningTagService).assignMeaningTag(any());

        // when
        MyBookUpdateResponse response = myBookWriteService.updateMyBookProperties(request);

        // then
        assertAll(
                () -> verify(myBookRepository).findById(request.getMyBookId()),
                () -> {
                    assertThat(myBook).isNotNull();
                    assertThat(myBook.getBook().getReadCount()).isEqualTo(originReadCount + 1);
                    assertThat(response.getStartDateOfPossession()).isEqualTo(request.getStartDateOfPossession());
                    assertThat(response.isExchangeable()).isEqualTo(request.isExchangeable());
                    assertThat(response.isShareable()).isEqualTo(request.isShareable());
                    assertThat(response.isShowable()).isEqualTo(request.isShowable());
                    assertThat(response.getReadStatus()).isEqualTo(request.getReadStatus());
                    assertThat(response.getMeaningTag().getQuote()).isEqualTo(request.getMeaningTag().getQuote());
                    assertThat(response.getMeaningTag().getColorCode()).isEqualTo(request.getMeaningTag().getColorCode());
                }
        );
    }

    @DisplayName("독서 상태를 완독에서 읽는 중으로 수정할 때, 도서의 readCount가 1 감소한다")
    @Test
    void updateMyBookReadStatusToReadingFromCompleted() {

        //given
        MybookUpdateServiceRequest request = MybookDtoTestData.createMyBookUpdateServiceRequest(LOGIN_ID, MYBOOK_ID)
                .readStatus(ReadStatus.READING)
                .build();

        MyBook myBook = MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBookBuilder()
                .readStatus(ReadStatus.COMPLETED)
                .build();

        Integer originReadCount = myBook.getBook().getReadCount();

        given(myBookRepository.findById(any())).willReturn(Optional.of(myBook));
        willDoNothing().given(meaningTagService).assignMeaningTag(any());

        // when
        MyBookUpdateResponse response = myBookWriteService.updateMyBookProperties(request);

        // then
        assertAll(
                () -> verify(myBookRepository).findById(request.getMyBookId()),
                () -> {
                    assertThat(myBook).isNotNull();
                    assertThat(myBook.getBook().getReadCount()).isEqualTo(originReadCount - 1);
                    assertThat(response.getStartDateOfPossession()).isEqualTo(request.getStartDateOfPossession());
                    assertThat(response.isExchangeable()).isEqualTo(request.isExchangeable());
                    assertThat(response.isShareable()).isEqualTo(request.isShareable());
                    assertThat(response.isShowable()).isEqualTo(request.isShowable());
                    assertThat(response.getReadStatus()).isEqualTo(request.getReadStatus());
                    assertThat(response.getMeaningTag().getQuote()).isEqualTo(request.getMeaningTag().getQuote());
                    assertThat(response.getMeaningTag().getColorCode()).isEqualTo(request.getMeaningTag().getColorCode());
                }
        );
    }

    @DisplayName("다른 유저의 마이북을 수정시, 예외가 발생한다.")
    @Test
    void occurExceptionWhenUpdateOtherUserMyBook() {

        //given
        MybookUpdateServiceRequest request = MybookDtoTestData.createMyBookUpdateServiceRequest(LOGIN_ID, MYBOOK_ID).build();
        MyBook myBook = MyBookFixture.COMMON_OTHER_USER_MYBOOK.getMyBook();

        given(myBookRepository.findById(any())).willReturn(
                Optional.ofNullable(myBook));

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> myBookWriteService.updateMyBookProperties(request))
                        .isInstanceOf(MyBookAccessDeniedException.class),
                () -> verify(myBookRepository).findById(request.getMyBookId())
        );
    }

}
