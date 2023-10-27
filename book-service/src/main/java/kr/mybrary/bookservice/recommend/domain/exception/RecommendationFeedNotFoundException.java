package kr.mybrary.bookservice.recommend.domain.exception;

import kr.mybrary.bookservice.global.exception.ApplicationException;

public class RecommendationFeedNotFoundException extends ApplicationException {

    private static final int STATUS = 404;
    private static final String ERROR_CODE = "RF-05";
    private static final String ERROR_MESSAGE = "추천 피드가 존재하지 않습니다.";

    public RecommendationFeedNotFoundException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }

}
