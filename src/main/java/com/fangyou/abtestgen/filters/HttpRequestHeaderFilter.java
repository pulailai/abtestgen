package com.fangyou.abtestgen.filters;


import com.fangyou.abtestgen.HeaderContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jimmy on 17/9/6.
 */
public class HttpRequestHeaderFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("HttpRequestHeaderFilter just supports HTTP requests");
        }
        HeaderContext.initializeContext((HttpServletRequest) request);
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
