package kr.mybrary.bookservice.recommend.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.mybrary.bookservice.recommend.RecommendationFeedDtoTestData;
import kr.mybrary.bookservice.recommend.domain.RecommendationFeedReadService;
import kr.mybrary.bookservice.recommend.domain.RecommendationFeedWriteService;
import kr.mybrary.bookservice.recommend.presentation.dto.request.RecommendationFeedCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ActiveProfiles("test")
@WebMvcTest(RecommendationFeedController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class RecommendationFeedControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationFeedWriteService recommendationFeedWriteService;

    @MockBean
    private RecommendationFeedReadService recommendationFeedReadService;

    private static final String LOGIN_ID = "LOGIN_USER_ID";

    @DisplayName("추천 피드를 작성한다.")
    @Test
    void createRecommendationFeed() throws Exception {

        // given
        RecommendationFeedCreateRequest request = RecommendationFeedDtoTestData.createRecommendationFeedCreateRequest();
        String requestJson = objectMapper.writeValueAsString(request);
        doNothing().when(recommendationFeedWriteService).create(any());

        // when
        ResultActions actions = mockMvc.perform(post("/api/v1/recommendation-feeds")
                .header("USER-ID", LOGIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("201 CREATED"))
                .andExpect(jsonPath("$.message").value("추천 피드를 작성하였습니다."))
                .andExpect(jsonPath("$.data").isEmpty());

        // document
        actions
                .andDo(document("create-recommendation-feed",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("recommendation-feed")
                                        .summary("추천 피드를 작성한다.")
                                        .requestSchema(Schema.schema("create_recommendation_feed_request_body"))
                                        .requestHeaders(
                                                headerWithName("USER-ID").description("사용자 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("myBookId").type(NUMBER).description("마이북 ID"),
                                                fieldWithPath("content").type(STRING).description("마이북 리뷰 별점"),
                                                fieldWithPath("recommendationTargetNames").type(ARRAY).description("마이북 리뷰 별점")
                                        )
                                        .responseSchema(Schema.schema("create_recommendation_feed_response_body"))
                                        .responseFields(
                                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                                fieldWithPath("data").type(OBJECT).description("응답 데이터").optional()
                                        ).build())));
    }

    @DisplayName("페이징을 통해 추천 피드를 조회한다.")
    @Test
    void getRecommendationFeedWithNoOffsetPaging() throws Exception {

        // given
        int limit = 10;
        Long cursor = 10L;
        given(recommendationFeedReadService.findRecommendationFeedWithNoOffsetPaging(any()))
                .willReturn(RecommendationFeedDtoTestData.createRecommendationFeedViewAllResponse());

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/recommendation-feeds")
                .param("limit", String.valueOf(limit))
                .param("cursor", String.valueOf(cursor))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200 OK"))
                .andExpect(jsonPath("$.message").value("추천 피드를 조회하였습니다."))
                .andExpect(jsonPath("$.data").isNotEmpty());

        // document
        actions
                .andDo(document("get-recommendation-feed-with-no-offset-paging",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("recommendation-feed")
                                        .summary("페이징을 통해 추천 피드를 조회한다.")
                                        .description("무한 스크롤 시, cursor의 값은 지난 응답의 lastRecommendationFeedId를 사용한다.\n" +
                                                "처음 추천 피드를 조회할 경우, cursor는 생략한다.\n" +
                                                "limit는 생략가능하며, 생략 시 10개로 설정된다.")
                                        .requestSchema(Schema.schema(
                                                "get_recommendation_feed_with_no_offset_paging_request_body"))
                                        .queryParameters(
                                                parameterWithName("cursor").type(SimpleType.NUMBER)
                                                        .description("마지막 추천 피드 ID"),
                                                parameterWithName("limit").type(SimpleType.NUMBER).description("조회 개수")
                                        )
                                        .responseSchema(Schema.schema(
                                                "get_recommendation_feed_with_no_offset_paging_response_body"))
                                        .responseFields(
                                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                                fieldWithPath("data.recommendationFeeds").type(ARRAY)
                                                        .description("추천 피드 목록"),
                                                fieldWithPath("data.recommendationFeeds[].content").type(STRING)
                                                        .description("추천 피드 내용"),
                                                fieldWithPath(
                                                        "data.recommendationFeeds[].recommendationTargetNames").type(
                                                        ARRAY).description("추천 피드 대상자 목록"),
                                                fieldWithPath("data.recommendationFeeds[].userId").type(STRING)
                                                        .description("추천 피드 작성자 ID"),
                                                fieldWithPath("data.recommendationFeeds[].nickname").type(STRING)
                                                        .description("추천 피드 작성자 닉네임"),
                                                fieldWithPath("data.recommendationFeeds[].profileImageUrl").type(STRING)
                                                        .description("추천 피드 작성자 프로필 이미지 URL"),
                                                fieldWithPath("data.recommendationFeeds[].myBookId").type(NUMBER)
                                                        .description("추천 피드 마이북 ID"),
                                                fieldWithPath("data.recommendationFeeds[].bookId").type(NUMBER)
                                                        .description("추천 피드 마이북의 책 ID"),
                                                fieldWithPath("data.recommendationFeeds[].title").type(STRING)
                                                        .description("추천 피드 책 제목"),
                                                fieldWithPath("data.recommendationFeeds[].thumbnailUrl").type(STRING)
                                                        .description("추천 피드 책 썸네일 URL"),
                                                fieldWithPath("data.recommendationFeeds[].isbn13").type(STRING)
                                                        .description("추천 피드 책 ISBN13"),
                                                fieldWithPath("data.recommendationFeeds[].authors").type(ARRAY)
                                                        .description("추천 피드 책 저자 목록"),
                                                fieldWithPath("data.recommendationFeeds[].holderCount").type(NUMBER)
                                                        .description("추천 피드 책 보유자 수"),
                                                fieldWithPath("data.recommendationFeeds[].interestCount").type(NUMBER)
                                                        .description("추천 피드 책 관심자 수"),
                                                fieldWithPath("data.lastRecommendationFeedId").type(NUMBER)
                                                        .description("마지막 추천 피드 ID")
                                        ).build())));
    }

    @DisplayName("페이징을 통해 추천 피드를 조회한다.")
    @Test
    void getRecommendationFeedWithNoOffsetPagingWhenInitialGet() throws Exception {

        // given
        int limit = 10;

        given(recommendationFeedReadService.findRecommendationFeedWithNoOffsetPaging(any()))
                .willReturn(RecommendationFeedDtoTestData.createRecommendationFeedViewAllResponse());

        // when
        ResultActions actions = mockMvc.perform(get("/api/v1/recommendation-feeds")
                .param("limit", String.valueOf(limit))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200 OK"))
                .andExpect(jsonPath("$.message").value("추천 피드를 조회하였습니다."))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

}