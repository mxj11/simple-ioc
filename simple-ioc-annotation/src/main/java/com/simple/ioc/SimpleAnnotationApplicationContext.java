package com.simple.ioc;

import com.simple.ioc.annotation.*;
import com.simple.ioc.utils.ClassUtil;
import com.simple.ioc.utils.CollectionUtils;
import com.simple.ioc.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleAnnotationApplicationContext {
    private static ConcurrentHashMap<String, Object> contains = new ConcurrentHashMap<>();

    public SimpleAnnotationApplicationContext(String packageName) {
        init(packageName);
    }

    private void initDependence() {
        if (CollectionUtils.isEmpty(contains)) {
            return;
        }
        contains.forEach((key, value) -> {
            Field[] declaredFields = value.getClass().getDeclaredFields();
            if (declaredFields != null) {
                for (Field field : declaredFields) {
                    if (field == null) {
                        continue;
                    }
                    initResourceAnnotation(value, field);
                    initAutoWiredAnnotation(value, field);
                }
            }
        });
    }

    private void initAutoWiredAnnotation(Object value, Field field) {
        AutoWired annotation = field.getAnnotation(AutoWired.class);
        if (annotation != null) {
            // 根据接口找到对应的实现类，并依赖注入
            // 1、获取属性的class
            Class<?> fieldClassType = field.getType();
            // 2、遍历容器，找到各个bean的接口，判断这个bean的接口的class是否与属性的class一致
            contains.forEach((key, object) -> {
                Class<?>[] interfaces = object.getClass().getInterfaces();
                for (Class clz : interfaces) {
                    // 2.1、如果一致，将容器的bean设置到这个属性中
                    if (clz == fieldClassType) {
                        field.setAccessible(true);
                        try {
                            field.set(value, object);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void initResourceAnnotation(Object value, Field field) {
        Resource annotation = field.getAnnotation(Resource.class);
        if (annotation != null) {
            String name = field.getName();
            Object fieldObject = contains.get(name);
            if (fieldObject != null) {
                field.setAccessible(true);
                try {
                    field.set(value, fieldObject);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
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
        initContainer(classes);
        initDependence();
    }

    private void initContainer(List<Class<?>> classes) {
        for (Class classInfo : classes) {
            if (classInfo == null) {
                continue;
            }
            // 获取类上带@Service/@Component/@Controller/@Repository注解的类
            List<Annotation> annotations = getAnnotations(classInfo);
            if (CollectionUtils.isEmpty(annotations)) {
                continue;
            }
            for (Annotation annotation : annotations) {
                if (annotation != null) {
                    String beanId = toLowerCase(classInfo.getSimpleName());
                    if (StringUtils.isEmpty(beanId)) {
                        continue;
                    }
                    // 放入IOC容器
                    try {
                        Object object = classInfo.newInstance();
                        contains.put(beanId, object);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private List<Annotation> getAnnotations(Class beanClass) {
        List<Annotation> annotations = new ArrayList<>();
        annotations.add(beanClass.getAnnotation(Service.class));
        annotations.add(beanClass.getAnnotation(Component.class));
        annotations.add(beanClass.getAnnotation(Controller.class));
        annotations.add(beanClass.getAnnotation(Repository.class));
        return annotations;
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
            return contains.get(beanId);
        }
        throw new RuntimeException("get bean failed");
    }
}
