package com.fpt.fms.aop.logging;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LogService {
    void requestLogging(HttpServletRequest request, Object body);

    void responseLogging(HttpServletRequest request, HttpServletResponse response, Object body);
}
