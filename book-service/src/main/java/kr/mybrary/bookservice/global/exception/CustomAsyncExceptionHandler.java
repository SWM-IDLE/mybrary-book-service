package kr.mybrary.bookservice.global.exception;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, @NotNull Object... params) {
        log.error("Async Method Error : {}, Cause By : {}", method.getName(), ex.getMessage());
    }
}