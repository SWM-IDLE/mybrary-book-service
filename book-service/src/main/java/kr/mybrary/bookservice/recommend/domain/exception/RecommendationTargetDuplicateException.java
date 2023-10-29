package kr.mybrary.bookservice.recommend.domain.exception;

import kr.mybrary.bookservice.global.exception.ApplicationException;

public class RecommendationTargetDuplicateException extends ApplicationException {

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "RF-02";
    private static final String ERROR_MESSAGE = "추천 대상 내에 중복이 존재합니다.";

    public RecommendationTargetDuplicateException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }
}
