package com.simple.ioc;

public interface AbstractApplicationContext {
    Object getBean(String beanId);

    <T> Object getBean(Class<T> clz);
}
