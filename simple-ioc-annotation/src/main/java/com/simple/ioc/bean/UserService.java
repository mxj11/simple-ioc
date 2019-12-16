package com.simple.ioc.bean;

import com.simple.ioc.Resource;
import com.simple.ioc.Service;

@Service
public class UserService {
    @Resource
    private OrderService orderService;

    public void test() {
        orderService.test();
    }
}
