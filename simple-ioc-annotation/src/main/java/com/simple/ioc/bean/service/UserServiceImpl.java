package com.simple.ioc.bean.service;

import com.simple.ioc.annotation.AutoWired;
import com.simple.ioc.annotation.Service;

@Service
public class UserServiceImpl implements UserService{
    @AutoWired
    private OrderService orderService;

    @Override
    public void test() {
        orderService.test();
    }
}
