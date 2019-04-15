package com.yucong.insidebuy;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yucong.App;
import com.yucong.insidebuy.repository.ActivityRepository;
import com.yucong.insidebuy.repository.GoodsModelRepository;
import com.yucong.insidebuy.repository.OrderRepository;
import com.yucong.insidebuy.repository.ShoppingCartRepository;
import com.yucong.insidebuy.repository.TypeRepository;
import com.yucong.insidebuy.service.GoodsDetailService;
import com.yucong.insidebuy.service.GoodsInfoService;
import com.yucong.insidebuy.service.OrderService;
import com.yucong.insidebuy.service.ShoppingCartService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class TestInsideBuyMethod {

    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private GoodsInfoService goodsInfoService;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private GoodsDetailService goodsDetailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private GoodsModelRepository goodsModelRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    public void test_transactional() {
        //
        try {
            // 购买商品，减去库存
            int count1 = goodsModelRepository.updateInventoryById(1l, 2);
            System.out.println("更新 count1 ：" + count1);

            // 取消订单，返回库存
            int count2 = goodsModelRepository.updateGoodsCountById(2l, 2);
            System.out.println("更新 count12：" + count2);

            throw new RuntimeException("自定义异常");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_main() {
        Properties properties = System.getProperties();
        for (String key : properties.stringPropertyNames()) {
            System.err.println(key + "=" + properties.getProperty(key));
        }

        Map<String, String> getenv = System.getenv();
        for (Entry<String, String> i : getenv.entrySet()) {
            System.err.println(i.getKey().toLowerCase() + ":" + i.getValue());
        }
    }


}
