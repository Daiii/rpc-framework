package cn.zhangbo.frameworke.rpc;

import cn.zhangbo.frameworke.rpc.annotation.RpcConsumer;
import cn.zhangbo.frameworke.rpc.annotation.RpcService;
import cn.zhangbo.frameworke.rpc.kernel.factory.ProxyFactoryBean;
import cn.zhangbo.frameworke.rpc.kernel.scanner.ClassScanner;
import cn.zhangbo.frameworke.rpc.registry.RegistryService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ComponentRegistrar extends ConfigurationClassPostProcessor implements ApplicationContextAware {

    private Class<?> startClass;

    private String[] scanPackages;

    private ResourceLoader resourceLoader;

    private Environment environment;

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        this.startClass = deduceMainApplicationClass();
        this.scanPackages = getScanPackages();

        registerServices(registry);
        registerConsumers(registry);
    }

    private void registerServices(BeanDefinitionRegistry registry) {
        RegistryService registryService = applicationContext.getBean(RegistryService.class);
        String serviceNameFormat = "%s#%s#%s";
        ClassScanner scanner = new ClassScanner(registry, false, environment, resourceLoader);
        Set<BeanDefinitionHolder> holders = scanner.doScan(RpcService.class, scanPackages);
        for (BeanDefinitionHolder holder : holders) {
            BeanDefinition beanDef = holder.getBeanDefinition();
        }
    }

    private void registerConsumers(BeanDefinitionRegistry registry) {
        DefaultBeanNameGenerator defaultBeanNameGenerator = new DefaultBeanNameGenerator();
        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanDefinitionName);
            if (beanDef.getBeanClassName() == null) {
                continue;
            }

            Class<?> beanClass = null;
            try {
                beanClass = Class.forName(beanDef.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(RpcConsumer.class)) {
                    Class<?> clz = field.getType();
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProxyFactoryBean.class);
                    builder.addConstructorArgValue(clz);
                    AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
                    registry.registerBeanDefinition(defaultBeanNameGenerator.generateBeanName(beanDefinition, registry), beanDefinition);
                }
            }
        }
    }

    private Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String[] getScanPackages() {
        AnnotationMetadata metadata = AnnotationMetadata.introspect(startClass);
        Set<String> result = new HashSet<>();
        addComponentScanningPackages(result, metadata);
        return result.toArray(new String[0]);
    }

    private void addComponentScanningPackages(Set<String> packages, AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(ComponentScan.class.getName(), true));
        if (attributes != null) {
            addPackages(packages, attributes.getStringArray("value"));
            addPackages(packages, attributes.getStringArray("basePackages"));
            addClasses(packages, attributes.getStringArray("basePackageClasses"));
        }

        // merge SpringBootApplication 上定义的扫描范围
        attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(SpringBootApplication.class.getName(), true));
        if (attributes != null) {
            addPackages(packages, attributes.getStringArray("scanBasePackages"));
            addClasses(packages, attributes.getStringArray("scanBasePackageClasses"));
        }

        if (packages.isEmpty()) {
            packages.add(org.springframework.util.ClassUtils.getPackageName(metadata.getClassName()));
        }
    }

    private void addPackages(Set<String> packages, String[] values) {
        if (values != null) {
            Collections.addAll(packages, values);
        }
    }

    private void addClasses(Set<String> packages, String[] values) {
        if (values != null) {
            for (String value : values) {
                packages.add(org.springframework.util.ClassUtils.getPackageName(value));
            }
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private String getHostname() {
        String hostname = "127.0.0.1";
        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return hostname;
    }
}
