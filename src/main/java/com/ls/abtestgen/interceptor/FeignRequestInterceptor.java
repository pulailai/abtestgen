package com.ls.abtestgen.interceptor;

import com.ls.abtestgen.HeaderContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by jimmy on 17/9/20.
 */
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {
        log.info("Start adding headers to the requestTemplate");
        final Map<String, String> headers = HeaderContext.getCurrentHeaderContext().getHeaders();
        if (headers == null || headers.isEmpty()) {
            log.warn("Current context headers is empty!");
            return;
        }
        for (Map.Entry<String, String> item : headers.entrySet()) {
            requestTemplate.header(item.getKey(), item.getValue());
        }
        log.info("Add to complete");
    }
}
