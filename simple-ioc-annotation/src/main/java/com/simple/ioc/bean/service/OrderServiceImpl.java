package com.simple.ioc.bean.service;

import com.simple.ioc.annotation.Service;

@Service
public class OrderServiceImpl implements OrderService, OrderTest {
    @Override
    public void test() {
        System.out.println("test");
    }
}
