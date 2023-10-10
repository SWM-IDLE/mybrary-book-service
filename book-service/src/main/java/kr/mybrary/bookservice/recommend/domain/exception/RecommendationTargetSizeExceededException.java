package kr.mybrary.bookservice.recommend.domain.exception;

import kr.mybrary.bookservice.global.exception.ApplicationException;

public class RecommendationTargetSizeExceededException extends ApplicationException {

    private final static int STATUS = 400;
    private final static String ERROR_CODE = "RF-01";
    private final static String ERROR_MESSAGE = "추천 대상은 최대 5개까지만 가능합니다.";

    public RecommendationTargetSizeExceededException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }
}
