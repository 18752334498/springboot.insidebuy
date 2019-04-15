package com.yucong.insidebuy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yucong.insidebuy.repository.GoodsModelRepository;

@Service
public class GoodsModelService {

    // 异常在A方法内抛出，则A方法就得加注解，类级别的@Transactional不管用
    // 多个方法嵌套调用，如果都有 @Transactional 注解，则产生事务传递，需要 Propagation.REQUIRED
    // 如果注解上只写 @Transactional 默认只对 RuntimeException 回滚，而非 Exception 进行回滚
    // 如果要对 checked Exceptions 进行回滚，则需要 @Transactional(rollbackFor = Exception.class)

    @Autowired
    private GoodsModelRepository goodsModelRepository;

    @Transactional(rollbackFor = Exception.class)
    public void test_transactional() throws Exception {

        // 库存 -2
        int count1 = goodsModelRepository.updateInventoryById(1l, 2);
        System.out.println("更新 count1 ：" + count1);

        // 库存 +2
        int count2 = goodsModelRepository.updateGoodsCountById(2l, 2);
        System.out.println("更新 count12：" + count2);

        throw new Exception("自定义异常");
    }

}
