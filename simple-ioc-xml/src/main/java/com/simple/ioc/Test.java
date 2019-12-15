package com.simple.ioc;

import com.simple.ioc.bean.User;

public class Test {
    public static void main(String[] args) {
        AbstractApplicationContext context = new SimpleXmlClassPathApplicationContext("bean.xml");
        User user = (User) context.getBean("user");
        System.out.println(user);
        User user1 = (User) context.getBean("user");
        System.out.println(user1);
    }
}
