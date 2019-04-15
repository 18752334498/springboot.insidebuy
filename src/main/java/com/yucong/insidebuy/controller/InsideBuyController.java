package com.yucong.insidebuy.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yucong.insidebuy.service.GoodsInfoService;
import com.yucong.insidebuy.service.GoodsModelService;

@RestController
@RequestMapping("buy")
public class InsideBuyController {

    @Autowired
    private GoodsModelService goodsModelService;
    @Autowired
    private GoodsInfoService goodsInfoService;


    @PostMapping("post")
    public String post_test(HttpServletRequest request) {
        System.out.println("请求方式：" + request.getMethod());
        System.out.println("获取路径：" + request.getContextPath());
        System.out.println("请求参数：" + request.getParameter("name"));
        return "success";
    }


    @RequestMapping("transactionl")
    public String test_transactional() {
        try {
            goodsModelService.test_transactional();
        } catch (Exception e) {
            System.out.println("===========================: " + e.getMessage());
            e.getCause();
        }
        return "success";
    }

    @RequestMapping("update")
    public String test_updateGoodsForManager() {
        goodsInfoService.updateGoodsForManager();
        return "success";
    }


}
