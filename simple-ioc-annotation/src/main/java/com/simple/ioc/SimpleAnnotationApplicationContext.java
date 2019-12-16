package com.simple.ioc;

import com.simple.ioc.utils.ClassUtil;
import com.simple.ioc.utils.CollectionUtils;
import com.simple.ioc.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleAnnotationApplicationContext {
    private static ConcurrentHashMap<String, Object> contains = new ConcurrentHashMap<>();

    public SimpleAnnotationApplicationContext(String packageName) {
        init(packageName);
    }

    private void initDependence(Object object) {
        if (!CollectionUtils.isEmpty(contains)) {
            contains.forEach((key,value) ->{
                Field[] declaredFields = value.getClass().getDeclaredFields();
                if (declaredFields != null) {
                    for (Field field : declaredFields) {
                        if (field == null) {
                            continue;
                        }
                        Resource annotation = field.getAnnotation(Resource.class);
                        if (annotation != null) {
                            String name = field.getName();
                            Object fieldObject = contains.get(name);
                            if (fieldObject != null) {
                                field.setAccessible(true);
                                try {
                                    field.set(object,fieldObject);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
        }
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
                try {
                    contains.put(beanId, classInfo.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
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
            initDependence(contains.get(beanId));
            return contains.get(beanId);
        }
        throw new RuntimeException("get bean failed");
    }
}
