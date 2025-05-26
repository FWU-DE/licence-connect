package com.fwu.lc_core.config.logging;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshotFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

import java.util.List;

import static org.hibernate.internal.util.collections.CollectionHelper.isEmpty;

@Configuration
@ConditionalOnClass({ContextRegistry.class, ContextSnapshotFactory.class})
@ConditionalOnProperty(value = "logging.context.fields", matchIfMissing = true)
public class MdcContextPropagationConfiguration {

    public MdcContextPropagationConfiguration(@Value("${logging.context.fields}") List<String> fields) {
        fields.forEach(claim -> ContextRegistry.getInstance()
                .registerThreadLocalAccessor(claim,
                        () -> MDC.get(claim),
                        value -> MDC.put(claim, value),
                        () -> MDC.remove(claim)));
        Hooks.enableAutomaticContextPropagation();
    }
}
