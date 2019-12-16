package com.simple.ioc;

import com.simple.ioc.bean.UserService;

public class Test {
    public static void main(String[] args) {
        SimpleAnnotationApplicationContext context = new SimpleAnnotationApplicationContext("com.simple.ioc");
        UserService userService = (UserService) context.getBean("userService");
        System.out.println(userService);
        userService.test();
    }
}
