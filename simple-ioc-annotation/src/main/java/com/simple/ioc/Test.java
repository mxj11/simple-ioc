package com.simple.ioc;

import com.simple.ioc.bean.controller.UserController;
import com.simple.ioc.bean.service.UserServiceImpl;

public class Test {
    public static void main(String[] args) {
        SimpleAnnotationApplicationContext context = new SimpleAnnotationApplicationContext("com.simple.ioc");
        UserController userController = (UserController) context.getBean("userController");
        System.out.println(userController);
        userController.test();
    }
}
