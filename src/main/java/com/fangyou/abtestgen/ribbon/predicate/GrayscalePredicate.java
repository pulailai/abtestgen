package com.fangyou.abtestgen.ribbon.predicate;


import com.fangyou.abtestgen.AbTestGenConfiguration;
import com.fangyou.abtestgen.Constants;
import com.fangyou.abtestgen.HeaderContext;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.yanglinkui.ab.dsl.Operation;
import com.yanglinkui.ab.dsl.Variable;
import com.yanglinkui.ab.dsl.service.ServiceContext;
import com.yanglinkui.ab.dsl.service.ServiceLexer;
import com.yanglinkui.ab.dsl.service.ServiceParser;
import com.yanglinkui.ab.dsl.service.Statements;
import io.jmnarloch.spring.cloud.ribbon.predicate.DiscoveryEnabledPredicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by jimmy on 17/9/7.
 */
@Slf4j
public class GrayscalePredicate extends DiscoveryEnabledPredicate {


    static LoadingCache<String, Statements> statementsCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Statements>() {
                public Statements load(String key) {
                    //解析器句柄
                    ServiceParser serviceParser = new ServiceParser(new ServiceLexer(key));
                    return serviceParser.statements();
                }
            });


    @Override
    protected boolean apply(DiscoveryEnabledServer server) {
        // globalDevserver用于测试环境，防止开发人员的自己的服务被ribbon选中
        if (StringUtils.isNotEmpty(AbTestGenConfiguration.globalDevserver)) {
            if (!server.getHost().equals(AbTestGenConfiguration.globalDevserver)) {
                log.info("skip1 server:server={}", server);
                return false;
            }
        }

        final Map<String, String> headers = HeaderContext.getCurrentHeaderContext().getHeaders();
        if (headers == null || headers.isEmpty()) {
            log.warn("Headers is empty");
            return true;
        }

        String value = headers.get(Constants.XFYSERVICE);
        if (StringUtils.isEmpty(value)) {
            log.warn("header[{}] is empty", Constants.XFYSERVICE);
            return true;
        }

        try {
            Statements statements = statementsCache.get(value);
            //获取server元数据
            final Map<String, String> metadata = server.getInstanceInfo().getMetadata();
            boolean result = false;
            for (final Map.Entry<String, String> entry : metadata.entrySet()) {
                String key = server.getInstanceInfo().getAppName() + "." + entry.getKey();
                Operation operation = statements.getOperation(key);
                if (operation != null) {
                    result = operation.interpret(new ServiceContext() {
                        @Override
                        public String getValue(Variable variable) {
                            return entry.getValue();
                        }
                    });
                    if (!result) {
                        log.info("skip2 server:server={}", server);
                        break;
                    }
                }
            }
            return result;
        } catch (Throwable e) {
            log.error("Parse header failure:header-key={},header-value={}", Constants.XFYSERVICE, value, e);
            log.info("skip3 server:server={}", server);
            return false;
        }
    }
}
