package kr.mybrary.bookservice.recommend.domain.exception;

import kr.mybrary.bookservice.global.exception.ApplicationException;

public class RecommendationTargetSizeLackException extends ApplicationException {

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "RF-03";
    private static final String ERROR_MESSAGE = "추천 대상은 최소 1개 이상 가능합니다.";

    public RecommendationTargetSizeLackException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }
}
