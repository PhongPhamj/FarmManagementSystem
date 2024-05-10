package com.fpt.fms.aop.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNullApi;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;

@ControllerAdvice
public class RequestBody extends RequestBodyAdviceAdapter {// call when request go to controller

   LogService logService;

   HttpServletRequest request;

    @Autowired
    public RequestBody(LogService logService, HttpServletRequest request) {
        this.logService = logService;
        this.request = request;
    }

    @Override
    public boolean supports(@Nullable MethodParameter methodParameter,@Nullable Type targetType,@Nullable Class<? extends HttpMessageConverter<?>> converterType) {

        return true;// false then not commit any change of request
    }

    @Override
    public Object afterBodyRead(@NotNull Object body, @NotNull HttpInputMessage inputMessage, @NotNull MethodParameter parameter, @NotNull Type targetType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        logService.requestLogging(request, body);
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }
}
