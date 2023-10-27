package kr.mybrary.bookservice.recommend.domain.exception;

import kr.mybrary.bookservice.global.exception.ApplicationException;

public class RecommendationFeedAccessDeniedException extends ApplicationException {

    private static final int STATUS = 403;
    private static final String ERROR_CODE = "RF-06";
    private static final String ERROR_MESSAGE = "추천 피드에 대해 접근할 수 없습니다.";

    public RecommendationFeedAccessDeniedException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
