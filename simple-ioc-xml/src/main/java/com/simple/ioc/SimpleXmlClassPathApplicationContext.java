package com.simple.ioc;


import com.simple.ioc.utils.CollectionUtils;
import com.simple.ioc.utils.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleXmlClassPathApplicationContext implements AbstractApplicationContext{
    private String path;

    private static ConcurrentHashMap<String, Object> contains = new ConcurrentHashMap<>();

    public SimpleXmlClassPathApplicationContext(String path) {
        this.path = path;
    }

    @Override
    public Object getBean(String beanId) {
        if (StringUtils.isEmpty(beanId)) {
            throw new IllegalArgumentException("beanId must not be empty");
        }
        if (contains.containsKey(beanId)) {
            System.out.println("直接从IOC容器获取Bean");
            return contains.get(beanId);
        }
        if (StringUtils.isEmpty(path)) {
            throw new RuntimeException("config path must not be empty");
        }
        try {
            // 读取xml文件获取Document对象
            Document document = getDocument();
            // 获取根标签
            Element rootElement = document.getRootElement();
            // 获取bean的类名
            String className = getClassName(beanId, rootElement);
            if (StringUtils.isEmpty(className)) {
                throw new RuntimeException("do not config class");
            }
            // 使用反射创建Bean对象
            Object object = newInstance(className);
            // 将对象放入IOC容器
            contains.put(beanId, object);
            return object;
        } catch (DocumentException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            System.out.println("occur exception,cause=" + e.getMessage());
        }
        throw new RuntimeException("getBean failed");
    }

    @Override
    public <T> Object getBean(Class<T> clz) {
        return null;
    }

    private Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> clz = Class.forName(className);
        if (clz == null) {
            throw new RuntimeException("getBean failed");
        }
        return clz.newInstance();
    }

    private String getClassName(String beanId, Element rootElement) {
        if (rootElement == null) {
            throw new RuntimeException("config file error,no root element");
        }
        List<Element> elements = rootElement.elements();
        if (CollectionUtils.isEmpty(elements)) {
            throw new RuntimeException("do not config any bean");
        }
        for (Element element : elements) {
            if (element == null) {
                continue;
            }
            String value = element.attributeValue("id");
            if (StringUtils.isEmpty(value)) {
                throw new RuntimeException("no bean");
            }
            if (!value.equals(beanId)) {
                throw new RuntimeException("not any bean match");
            }
            return element.attributeValue("class");
        }
        return null;
    }

    private Document getDocument() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        return saxReader.read(inputStream);
    }
}
