package com.fangyou.abtestgen;

import com.fangyou.abtestgen.interceptor.FeignRequestInterceptor;
import com.fangyou.abtestgen.filters.HttpRequestHeaderFilter;
import com.fangyou.abtestgen.ribbon.rule.GrayscaleRule;
import feign.Feign;
import feign.RequestInterceptor;
import io.jmnarloch.spring.cloud.ribbon.rule.DiscoveryEnabledRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;

/**
 * Created by jimmy on 17/9/20.
 */
@Configuration
@Slf4j
public class AbTestGenConfiguration {


    @Value("${fangyou.zuul.devserver:}")
    public String devserver;

    // globalDevserver用于测试环境，防止开发人员的自己的服务被ribbon选中
    public static String globalDevserver;

    @PostConstruct
    private void init() {
        globalDevserver = this.devserver;
    }


    /**
     * header filter
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(value = "fangyou.header.filter.enabled", matchIfMissing = true)
    public FilterRegistrationBean filterRegistration() {
        log.info("Initialize HttpRequestHeaderFilter");
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpRequestHeaderFilter());
        registration.addUrlPatterns("/*");
        registration.setName("httpRequestHeaderFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    /**
     * 灰度规则
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DiscoveryEnabledRule metadataCustomRule() {
        return new GrayscaleRule();
    }


    /**
     * feign interceptor
     *
     * @return RequestInterceptor
     * @ConditionalOnClass 当类路径下有指定的类的条件下才加载当前配置类
     * @ConditionalOnProperty：当指定的属性等于指定的值的情况下加载当前配置类
     * @matchIfMissing = true表示如果没有在application.properties设置该属性，则默认为条件符合
     */
    @Bean
    @ConditionalOnClass({Feign.class})
    @ConditionalOnProperty(value = "fangyou.feign.interceptor.enabled", matchIfMissing = true)
    public RequestInterceptor feignRequestInterceptor() {
        log.info("Initialize feignRequestInterceptor");
        return new FeignRequestInterceptor();
    }


}
