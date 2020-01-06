package com.simple.ioc.bean.controller;

import com.simple.ioc.annotation.AutoWired;
import com.simple.ioc.annotation.Controller;
import com.simple.ioc.bean.service.UserService;

/**
 * 功能描述
 *
 * @author : 刘向昭
 * @date : 2020-01-04
 */
@Controller
public class UserController {
    @AutoWired
    private UserService userService;

    public void test() {
        userService.test();
    }
}
