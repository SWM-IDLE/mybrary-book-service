package kr.mybrary.bookservice.recommend.domain.exception;

import kr.mybrary.bookservice.global.exception.ApplicationException;

public class RecommendationTargetLengthExceededException extends ApplicationException {

    private static final int STATUS = 400;
    private static final String ERROR_CODE = "RF-07";
    private static final String ERROR_MESSAGE = "추천 대상은 15자 이하로 작성해주세요.";

    public RecommendationTargetLengthExceededException() {
        super(STATUS, ERROR_CODE, ERROR_MESSAGE);
    }
}