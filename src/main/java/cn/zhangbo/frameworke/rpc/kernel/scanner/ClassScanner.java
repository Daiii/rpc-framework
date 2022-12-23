package cn.zhangbo.frameworke.rpc.kernel.scanner;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ClassScanner extends ClassPathBeanDefinitionScanner {

    public ClassScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment, ResourceLoader resourceLoader) {
        super(registry, useDefaultFilters, environment, resourceLoader);
    }

    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

    public <T extends Annotation> Set<BeanDefinitionHolder> doScan(Class<T> annotation, String... basePackages) {
        addIncludeFilter(new AnnotationTypeFilter(annotation));
        return super.doScan(basePackages);
    }
}