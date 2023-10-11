package kr.mybrary.bookservice.recommend.domain.exception;

import kr.mybrary.bookservice.global.exception.ApplicationException;

public class RecommendationTargetSizeLackException extends ApplicationException {

    private final static int STATUS = 400;
    private final static String ERROR_CODE = "RF-03";
    private final static String ERROR_MESSAGE = "추천 대상은 최소 1개 이상 가능합니다.";

    public RecommendationTargetSizeLackException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }
}
