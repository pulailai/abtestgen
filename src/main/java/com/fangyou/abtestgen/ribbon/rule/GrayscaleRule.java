package com.fangyou.abtestgen.ribbon.rule;


import com.fangyou.abtestgen.ribbon.predicate.GrayscalePredicate;
import io.jmnarloch.spring.cloud.ribbon.predicate.DiscoveryEnabledPredicate;
import io.jmnarloch.spring.cloud.ribbon.rule.DiscoveryEnabledRule;

/**
 * Created by jimmy on 17/9/15.
 */
public class GrayscaleRule extends DiscoveryEnabledRule {


    /**
     * Creates new instance of {@link GrayscaleRule}.
     */
    public GrayscaleRule() {
        this(new GrayscalePredicate());
    }


    /**
     * Creates new instance of {@link DiscoveryEnabledRule} class with specific predicate.
     *
     * @param discoveryEnabledPredicate the discovery enabled predicate
     */
    public GrayscaleRule(DiscoveryEnabledPredicate discoveryEnabledPredicate) {
        super(discoveryEnabledPredicate);
    }


}
