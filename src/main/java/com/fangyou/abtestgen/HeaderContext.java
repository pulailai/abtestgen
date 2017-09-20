package com.fangyou.abtestgen;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmy on 17/9/15.
 */
@Slf4j
public class HeaderContext {


    @Getter
    private Map<String, String> headers;

    private final static ThreadLocal<HeaderContext> threadLocal = new ThreadLocal<HeaderContext>();


    private HeaderContext() {
        super();
    }

    /**
     * 初始化headers
     *
     * @param request
     */
    public static void initializeContext(HttpServletRequest request) {
        if (request == null) {
            log.warn("HttpServletRequest is empty！");
            return;
        }
        HeaderContext headerContext = new HeaderContext();
        headerContext.headers = new HashMap<String, String>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            headerContext.headers.put(key, value);
        }
        threadLocal.set(headerContext);
    }


    /**
     * 获取当前headers
     *
     * @return
     */
    public static HeaderContext getCurrentHeaderContext() {
        return threadLocal.get();
    }


}
