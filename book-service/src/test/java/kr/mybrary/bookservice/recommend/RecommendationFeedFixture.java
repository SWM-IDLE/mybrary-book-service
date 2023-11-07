package kr.mybrary.bookservice.recommend;

import java.util.ArrayList;
import java.util.Arrays;
import kr.mybrary.bookservice.mybook.MyBookFixture;
import kr.mybrary.bookservice.mybook.persistence.MyBook;
import kr.mybrary.bookservice.recommend.persistence.RecommendationFeed;
import kr.mybrary.bookservice.recommend.persistence.RecommendationTarget;
import kr.mybrary.bookservice.recommend.persistence.RecommendationTargets;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RecommendationFeedFixture {

    COMMON_LOGIN_USER_RECOMMENDATION_FEED(1L, MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook(), "LOGIN_USER_ID", "CONTENT EXAMPLE",
            new RecommendationTargets(new ArrayList<>(Arrays.asList(RecommendationTarget.builder().targetName("TARGET_NAME_1").build(),
                    RecommendationTarget.builder().targetName("TARGET_NAME_2").build())))),
    COMMON_OTHER_USER_RECOMMENDATION_FEED(1L, MyBookFixture.COMMON_LOGIN_USER_MYBOOK.getMyBook(), "OTHER_USER_ID", "CONTENT EXAMPLE",
            new RecommendationTargets(new ArrayList<>(Arrays.asList(RecommendationTarget.builder().targetName("TARGET_NAME_1").build(),
                    RecommendationTarget.builder().targetName("TARGET_NAME_2").build()))));

    private final Long id;
    private final MyBook myBook;
    private final String userId;
    private final String content;
    private final RecommendationTargets recommendationTargets;

    public RecommendationFeed getRecommendationFeed() {
        return RecommendationFeed.builder()
                .id(id)
                .myBook(myBook)
                .userId(userId)
                .content(content)
                .recommendationTargets(recommendationTargets)
                .build();
    }
}
