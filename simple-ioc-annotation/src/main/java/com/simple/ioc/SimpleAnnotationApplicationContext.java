package com.simple.ioc;

import com.simple.ioc.utils.ClassUtil;
import com.simple.ioc.utils.CollectionUtils;
import com.simple.ioc.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleAnnotationApplicationContext {
    private static ConcurrentHashMap<String, Class<?>> contains = new ConcurrentHashMap<String, Class<?>>();

    public SimpleAnnotationApplicationContext(String packageName) {
        init(packageName);
    }

    private void init(String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            throw new RuntimeException("packageName must not be empty");
        }
        // 扫包，获取包下所有的类
        List<Class<?>> classes = ClassUtil.getClasses(packageName);
        if (CollectionUtils.isEmpty(classes)) {
            throw new RuntimeException("do not find any class");
        }
        for (Class classInfo : classes) {
            if (classInfo == null) {
                continue;
            }
            // 获取类上带@Service注解的类
            Annotation serviceAnnotation = classInfo.getAnnotation(Service.class);
            if (serviceAnnotation != null) {
                String beanId = toLowerCase(classInfo.getSimpleName());
                if (StringUtils.isEmpty(beanId)) {
                    continue;
                }
                // 放入IOC容器
                contains.put(beanId, classInfo);
            }
        }
    }

    private String toLowerCase(String name) {
        if (Character.isLowerCase(name.charAt(0))) {
            return name;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(name.charAt(0))).append(name.substring(1)).toString();
        }
    }

    public Object getBean(String beanId) {
        if (StringUtils.isEmpty(beanId)) {
            throw new RuntimeException("beanId must not be empty");
        }
        if (contains.containsKey(beanId)) {
            try {
                return contains.get(beanId).newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("get bean failed");
            }
        }
        throw new RuntimeException("get bean failed");
    }
}
