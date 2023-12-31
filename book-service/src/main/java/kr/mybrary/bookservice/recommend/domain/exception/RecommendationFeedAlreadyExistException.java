package kr.mybrary.bookservice.recommend.domain.exception;

import kr.mybrary.bookservice.global.exception.ApplicationException;

public class RecommendationFeedAlreadyExistException extends ApplicationException {

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "RF-04";
    private static final String ERROR_MESSAGE = "이미 추천 피드가 존재합니다.";

    public RecommendationFeedAlreadyExistException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }
}
