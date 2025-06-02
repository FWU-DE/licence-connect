package com.fwu.lc_core.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ClassNameRetriever {

    public List<String> getAllClassNames(ApplicationContext context) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));

        String basePackage = context.getBeansWithAnnotation(SpringBootApplication.class)
                .values().iterator().next().getClass().getPackage().getName();

        Set<BeanDefinition> beans = provider.findCandidateComponents(basePackage);

        return beans.stream()
                .map(BeanDefinition::getBeanClassName)
                .collect(Collectors.toList());
    }

}
