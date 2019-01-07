package com.wj.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date 2018/01/07
 **/

@Component
public class SpringContext implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(SpringContext.class);

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.context = applicationContext;
    }

    public static ApplicationContext getContext() {
        if (SpringContext.context == null) {
            logger.warn("context is null");
            return null;
        }
        return SpringContext.context;
    }

    public static<T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static<T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static<T> T getBean(String name) {
        if (!context.containsBean(name)) {
            logger.warn("bean is empty, name={}", name);
            return null;
        }
        return (T)context.getBean(name);
    }
}
