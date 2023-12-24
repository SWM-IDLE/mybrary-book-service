package kr.mybrary.bookservice.booksearch.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import kr.mybrary.bookservice.booksearch.BookSearchDtoTestData;
import kr.mybrary.bookservice.booksearch.domain.dto.request.BookListByCategorySearchServiceRequest;
import kr.mybrary.bookservice.booksearch.domain.dto.request.BookSearchServiceRequest;
import kr.mybrary.bookservice.booksearch.domain.exception.BookSearchResultNotFoundException;
import kr.mybrary.bookservice.booksearch.presentation.dto.response.BookListByCategorySearchResultResponse;
import kr.mybrary.bookservice.booksearch.presentation.dto.response.BookListByCategorySearchResultWithBookInfoResponse;
import kr.mybrary.bookservice.booksearch.presentation.dto.response.BookSearchDetailResponse;
import kr.mybrary.bookservice.booksearch.presentation.dto.response.BookSearchResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
@RestClientTest(value = AladinBookSearchApiService.class)
class AladinBookSearchApiServiceTest {

    private static final String BOOK_SEARCH_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";
    private static final String BOOK_DETAIL_SEARCH_URL = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx";
    private static final String BOOK_LIST_BY_CATEGORY_SEARCH_URL = "http://www.aladin.co.kr/ttb/api/ItemList.aspx";
    private static final String JSON_FILE_PATH = "src/test/resources/aladinapi/";

    @Autowired
    private RestTemplate restTemplate;
    private AladinBookSearchApiService aladinBookSearchApiService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setup() {
        aladinBookSearchApiService = new AladinBookSearchApiService(restTemplate);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @DisplayName("알라딘 도서 검색의 결과는 최대 20권이며, 20권이 넘어가면 expectNextRequestUrl이 존재한다.")
    @Test
    void searchWithKeywordAndResultMoreThan20() throws IOException {

        // given
        BookSearchServiceRequest request = BookSearchServiceRequest.of("Docker", "accuracy", 1);
        String expectResult = readJsonFile("resultMoreThan20FromKeyword.json");
        String expectNextRequestUrl = "/books/search?keyword=Docker&page=2&sort=accuracy";

        mockServer
                .expect(requestTo(BOOK_SEARCH_URL
                        + "?TTBKey&Output=js&Version=20131101&Query=Docker&Start=1&MaxResults=20&Cover=MidBig&Sort=accuracy"))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        BookSearchResultResponse bookSearchResultResponse = aladinBookSearchApiService.searchWithKeyword(request);

        // then
        mockServer.verify();
        assertAll(
                () -> assertThat(bookSearchResultResponse.getBookSearchResult().size()).isEqualTo(10),
                () -> assertThat(bookSearchResultResponse.getNextRequestUrl()).isEqualTo(expectNextRequestUrl)
        );
    }

    @DisplayName("알라딘 도서 검색의 결과가 20권 이하이면, expectNextRequestUrl는 빈 값이다.")
    @Test
    void searchWithKeywordAndResultLessThan20() throws IOException {

        // given
        BookSearchServiceRequest request = BookSearchServiceRequest.of("Docker", "accuracy", 2);
        String expectResult = readJsonFile("resultLessThan20FromKeyword.json");
        String expectNextRequestUrl = "";

        mockServer
                .expect(requestTo(BOOK_SEARCH_URL
                        + "?TTBKey&Output=js&Version=20131101&Query=Docker&Start=2&MaxResults=20&Cover=MidBig&Sort=accuracy"))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        BookSearchResultResponse bookSearchResultResponse = aladinBookSearchApiService.searchWithKeyword(request);

        // then
        mockServer.verify();
        assertAll(
                () -> assertThat(bookSearchResultResponse.getBookSearchResult().size()).isLessThan(20),
                () -> assertThat(bookSearchResultResponse.getNextRequestUrl()).isEqualTo(expectNextRequestUrl)
        );
    }

    @DisplayName("알라딘 도서 조회시, isbn13이 비어있으면 조회 목록에서 제외한다.")
    @Test
    void excludeResponseWhenISBN13IsBlank() throws IOException, URISyntaxException {

        // given
        BookSearchServiceRequest request = BookSearchServiceRequest.of("알라", "accuracy", 10);
        String expectResult = readJsonFile("resultHasBlankISBN13.json");
        String expectNextRequestUrl = "";

        mockServer
                .expect(requestTo(BOOK_SEARCH_URL +
                        "?TTBKey&Output=js&Version=20131101&Query=%EC%95%8C%EB%9D%BC&Start=10&MaxResults=20&Cover=MidBig&Sort=accuracy"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        BookSearchResultResponse bookSearchResultResponse = aladinBookSearchApiService.searchWithKeyword(request);

        // then
        mockServer.verify();
        assertAll(
                () -> assertThat(bookSearchResultResponse.getBookSearchResult()).hasSize(10),
                () -> assertThat(bookSearchResultResponse.getNextRequestUrl()).isEqualTo(expectNextRequestUrl)
        );
    }

    @DisplayName("알라딘의 최대 도서 검색 권수는 200권이다. 200권 이상 검색시 expectNextRequestUrl은 빈 값이다.")
    @Test
    void searchWithKeywordAndLimit200() throws IOException {

        // given
        BookSearchServiceRequest request = BookSearchServiceRequest.of("알라", "accuracy", 10);
        String expectResult = readJsonFile("resultOverLimit200FromKeyword.json");
        String expectNextRequestUrl = "";

        mockServer
                .expect(requestTo(BOOK_SEARCH_URL
                        + "?TTBKey&Output=js&Version=20131101&Query=%EC%95%8C%EB%9D%BC&Start=10&MaxResults=20&Cover=MidBig&Sort=accuracy"))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        BookSearchResultResponse bookSearchResultResponse = aladinBookSearchApiService.searchWithKeyword(request);

        // then
        mockServer.verify();
        assertAll(
                () -> assertThat(bookSearchResultResponse.getBookSearchResult().size()).isLessThan(20),
                () -> assertThat(bookSearchResultResponse.getNextRequestUrl()).isEqualTo(expectNextRequestUrl)
        );
    }

    @DisplayName("도서 검색 결과가 없으면 빈 응답을 반환한다.")
    @Test
    void searchWithKeywordAndResultEmpty() throws IOException {

        // given
        BookSearchServiceRequest request = BookSearchServiceRequest.of("JPA알라", "accuracy", 1);
        String expectResult = readJsonFile("resultEmptyFromKeyword.json");

        mockServer
                .expect(requestTo(BOOK_SEARCH_URL
                        + "?TTBKey&Output=js&Version=20131101&Query=JPA%EC%95%8C%EB%9D%BC&Start=1&MaxResults=20&Cover=MidBig&Sort=accuracy"))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        BookSearchResultResponse response = aladinBookSearchApiService.searchWithKeyword(request);

        // then
        mockServer.verify();
        assertAll(
                () -> assertThat(response.getBookSearchResult()).isEmpty(),
                () -> assertThat(response.getNextRequestUrl()).isEqualTo("")
        );
    }

    @DisplayName("알라딘 도서 상세 조회 한다.")
    @Test
    void searchBookDetailWithISBN() throws IOException {

        // given
        BookSearchServiceRequest request = BookSearchServiceRequest.of("9788965402602");
        String expectResult = readJsonFile("bookDetailResultFromISBN.json");

        mockServer
                .expect(requestTo(BOOK_DETAIL_SEARCH_URL
                        + "?TTBKey&Output=js&Version=20131101&ItemId=9788965402602&Cover=Big&ItemIdType=ISBN13&OptResult=packing,ratingInfo,authors,fulldescription,Toc"))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        BookSearchDetailResponse bookSearchDetailResponse = aladinBookSearchApiService.searchBookDetailWithISBN(request);

        // then
        mockServer.verify();
        assertAll(
                () -> assertThat(bookSearchDetailResponse.getIsbn13()).isEqualTo("9788965402602"),
                () -> assertThat(bookSearchDetailResponse.getIsbn10()).isEqualTo("8965402603")
        );
    }

    @DisplayName("알라딘 도서 상세 조회 시, 결과가 없으면 예외가 발생한다.")
    @Test
    void searchBookDetailWithISBNAndResultEmpty() throws IOException {

        // given
        BookSearchServiceRequest request = BookSearchServiceRequest.of("978898078297011");
        String expectResult = readJsonFile("aladinBookNotFoundError.json");

        mockServer
                .expect(requestTo(BOOK_DETAIL_SEARCH_URL
                        + "?TTBKey&Output=js&Version=20131101&ItemId=978898078297011&Cover=Big&ItemIdType=ISBN13&OptResult=packing,ratingInfo,authors,fulldescription,Toc"))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when, then
        assertThatThrownBy(() -> aladinBookSearchApiService.searchBookDetailWithISBN(request))
                .isInstanceOf(BookSearchResultNotFoundException.class);
        mockServer.verify();
    }

    @DisplayName("카테고리에 따른 알라딘 도서 리스트를 조회한다.")
    @Test
    void searchBookListByCategory() throws IOException {

        // given
        BookListByCategorySearchServiceRequest request = BookSearchDtoTestData.createBookListSearchServiceRequest();
        String expectResult = readJsonFile("bookListByCategoryResult.json");

        mockServer
                .expect(requestTo(BOOK_LIST_BY_CATEGORY_SEARCH_URL
                        + "?TTBKey&QueryType=bestseller&MaxResults=10&Start=1&Output=js&Version=20131101&CategoryId=0&SearchTarget=BOOK&Cover=Big"))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        BookListByCategorySearchResultResponse response = aladinBookSearchApiService.searchBookListByCategory(request);

        // then
        mockServer.verify();
        assertAll(
                () -> assertThat(response.getBooks()).hasSize(10)
        );
    }

    @DisplayName("카테고리에 따른 알라딘 도서 리스트를 도서 정보와 함께 조회한다.")
    @Test
    void searchBookListByCategoryWithBookInfo() throws IOException {

        // given
        BookListByCategorySearchServiceRequest request = BookSearchDtoTestData.createBookListSearchServiceRequest();
        String expectResult = readJsonFile("bookListByCategoryResult.json");

        mockServer
                .expect(requestTo(BOOK_LIST_BY_CATEGORY_SEARCH_URL
                        + "?TTBKey&QueryType=bestseller&MaxResults=10&Start=1&Output=js&Version=20131101&CategoryId=0&SearchTarget=BOOK&Cover=Big"))
                .andRespond(withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        BookListByCategorySearchResultWithBookInfoResponse response = aladinBookSearchApiService
                .searchBookListByCategoryWithBookInfo(request);

        // then
        mockServer.verify();
        assertAll(
                () -> assertThat(response.getBooks()).hasSize(10)
        );
    }


    private String readJsonFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH + fileName)));
    }
}