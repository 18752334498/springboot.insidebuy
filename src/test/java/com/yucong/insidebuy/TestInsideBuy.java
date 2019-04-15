package com.yucong.insidebuy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yucong.App;
import com.yucong.insidebuy.entity.Activity;
import com.yucong.insidebuy.entity.GoodsInfo;
import com.yucong.insidebuy.entity.GoodsModel;
import com.yucong.insidebuy.entity.Order;
import com.yucong.insidebuy.entity.ShoppingCart;
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
public class TestInsideBuy {

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
    public void test_findAll() {
        typeRepository.findAll();
    }

    @Test
    public void test_findActivityByEndTime() {
        Activity activity = activityRepository.findActivityByEndTime(new Date()).get(0);
        System.out.println(activity.getEndTime());
    }

    /**
     * <span>内购活动首页接口</span>
     * 
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    @Test
    public void test_findGoodsList() {

        List<Map<String, Object>> list = goodsInfoService.findGoodsList();

        JSONObject result = new JSONObject();

        result.put("data", dataProcess(list));

        Integer num = shoppingCartRepository.findNumOfCartByPhoneId(18752334498l);
        System.out.println("购物车数量是： " + num);
        result.put("shoppingCart", num);

        Activity activity = activityRepository.findAll().get(0);
        System.out.println("活动说明： " + activity.getStartIntroduce());
        result.put("activity", activity.getStartIntroduce());

        System.out.println(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }

    /**
     * <span>在列表页面点击某个商品产看详情，此页面有加入购物车功能</span>
     * 
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    @Test
    public void test_findGoodsDetail() {
        List<Map<String, Object>> list = goodsDetailService.findGoodsDetail();

        List<String> goodsModel = new ArrayList<>();
        for (Map<String, Object> map : list) {
            goodsModel.add(String.valueOf(map.get("goods_model")));
        }

        JSONObject result = new JSONObject();
        result.put("data", list);
        result.put("model", goodsModel);
        System.out.println(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }

    /**
     * <h3>在列表页面点击某个商品产看详情，点击加入购物车按钮，触发在购物车添加商品事件</h3>
     * <h3>客户端传参：添加商品数量，用户ID，商品ID，商品型号ID</h3>
     * 
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    @Test
    public void test_saveShoppingCartSingle() {
        // 方法一，用于在单个商品详情页面进行的新增操作，此操作只有一个商品增加。
        // 新增之前要根据 phone_id,goods_info_id，goods_model_id 来查看购物新是否存在

        ShoppingCart cart = shoppingCartRepository.findByPhoneInfoModelId(18752334433l, 12l, 1l);
        if (cart == null) {
            cart = new ShoppingCart();
            GoodsInfo goodsInfo = new GoodsInfo();
            goodsInfo.setId(55l);
            GoodsModel goodsModel = new GoodsModel();
            goodsModel.setId(55l);

            cart.setPhoneId(18752334455l);
            cart.setGoodsCount(55);
            cart.setGoodsInfo(goodsInfo);
            cart.setGoodsModel(goodsModel);
        } else {
            cart.setGoodsCount(cart.getGoodsCount() + 100);
        }
        shoppingCartRepository.save(cart);
    }

    @Test
    public void test_saveShoppingCartBatch() {

        // 方法二
        // List<ShoppingCart> carts = getCarts();
        // System.out.println(carts);
        // shoppingCartRepository.saveAll(carts);

        // 方法三
        String aa =
                "{\"carts\":[{\"goodsInfoId\":\"1\",\"goodsModelId\":\"1\",\"goodsCount\":\"11\",\"phoneId\":\"18752334433\"},{\"goodsInfoId\":\"2\",\"goodsModelId\":\"2\",\"goodsCount\":\"22\",\"phoneId\":\"18752334422\"}]}";
        JSONObject jsonObject = JSON.parseObject(aa);
        Object object = jsonObject.get("carts");
        List<ShoppingCart> carts2 = parseJsonToListForCart(object);
        System.out.println(carts2);
        shoppingCartRepository.saveAll(carts2);
    }

    /**
     * <h3>购物车接口</h3>
     * <h3>此外，在购物车有修改和删除功能</h3>
     * 
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    @Test
    public void test_findShoppingCartList() {
        List<Map<String, Object>> list = shoppingCartService.findShoppingCartList();
        System.out.println(JSON.toJSONString(list));
    }

    /**
     * <h3>进入购物车后，对商品进行删除</h3>
     *
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    @Test
    public void test_deleteShoppingByCartId() {
        Long cartId = 202l;
        int count = shoppingCartRepository.deleteByCartId(cartId);
        System.out.println("购物车删除了：" + count);
    }

    /**
     * <h3>点击购物车后，对商品进行更新</h3>
     * <h3>购物车的更新只能更新两种数据，goodsCount和goodsModel</h3>
     *
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    @Test
    public void test_updateShoppingByCartId() {
        Long cartId = 3l;
        Integer goodsCount = 100;
        Long goodsModelId = 4l;
        GoodsModel goodsModel = new GoodsModel();
        goodsModel.setId(goodsModelId);

        ShoppingCart cart = shoppingCartRepository.findById(cartId).get();
        System.out.println(cart.getId() + ";" + cart.getPhoneId());

        cart.setId(cartId);
        cart.setGoodsCount(goodsCount);
        cart.setGoodsModel(goodsModel);
        shoppingCartRepository.save(cart);

    }

    /**
     * <span>我的订单接口</span>
     * 
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    @Test
    public void test_findOrderList() {
        List<Map<String, Object>> list = orderService.findOrderList();
        List<String> orderGroups = list.stream().map(m -> String.valueOf(m.get("order_group"))).distinct().collect(Collectors.toList());

        List<Object> result = new ArrayList<>();
        for (String orderGroup : orderGroups) {
            result.add(list.stream().filter(m -> orderGroup.equals(m.get("order_group"))).collect(Collectors.toList()));
        }
        System.out.println(JSON.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }

    /**
     * <span>保存一个或者多个订单</span>
     * 
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void test_saveOrders() {
        String aa = "{\"orders\":[{\"goodsInfoId\":\"1\",\"goodsModelId\":\"1\",\"goodsCount\":\"3\",\"phoneId\":\"18752334498\"},"
                + "{\"goodsInfoId\":\"1\",\"goodsModelId\":\"2\",\"goodsCount\":\"1\",\"phoneId\":\"18752334498\"}],"
                + "\"info\":{\"address\":\"nanjing\",\"businessHall\":\"yuhuatai\",\"phoneNum\":\"18752334498\",\"username\":\"Tony\"}}";

        JSONObject jsonObject = JSONObject.parseObject(aa);

        // 参数解析
        List<Order> orderList = parseJsonToListForSaveOrder(jsonObject);

        // 循环对每个订单判断和保存
        for (Order order : orderList) {

            // 查出各种限制条件，只有库存数量是变值
            Map<String, Object> limit = orderService.queryAllLimitCount(order.getGoodsInfo().getId(), order.getGoodsModel().getId());
            int preBuyCount = order.getGoodsCount();
            int orderedCount = Integer.parseInt(String.valueOf(limit.get("orderedCount")));
            int actLimitCount = Integer.parseInt(String.valueOf(limit.get("actLimitCount")));
            int inventory = Integer.parseInt(String.valueOf(limit.get("inventory")));
            int modelLimitCount = Integer.parseInt(String.valueOf(limit.get("modelLimitCount")));

            System.out.println("preBuyCount:" + preBuyCount);
            System.out.println("orderedCount:" + orderedCount);
            System.out.println("modelLimitCount:" + modelLimitCount);
            System.out.println("actLimitCount:" + actLimitCount);
            System.out.println("inventory:" + inventory);

            if (preBuyCount > modelLimitCount || (preBuyCount + orderedCount) > modelLimitCount) {
                System.out.println("产品限购");
                throw new RuntimeException("产品限购");
            }
            if (preBuyCount > inventory) {
                System.out.println("库存不足");
                throw new RuntimeException("库存不足");
            }
            if (preBuyCount > actLimitCount || (preBuyCount + orderedCount) > actLimitCount) {
                System.out.println("活动限购");
                throw new RuntimeException("活动限购");
            }

            // 如果条件都满足，就先从库存减去 preBuyCount，再将订单插入数据库
            // 如果删除失败就意味着库存不足，或者其他网络原因
            int count = goodsModelRepository.updateInventoryById(order.getGoodsModel().getId(), preBuyCount);
            System.out.println("删除了：  " + count);
            orderRepository.save(order);
            System.out.println("==================== success ====================");
        }

    }

    @SuppressWarnings("unchecked")
    private List<Order> parseJsonToListForSaveOrder(JSONObject jsonObject) {
        JSONObject infoMap = (JSONObject) jsonObject.get("info");

        // 购买者信息校验
        String username, address, businessHall = null;
        Long phoneNum = null;
        try {
            username = infoMap.getString("username");
            address = infoMap.getString("address"); // 派送地址非必填
            businessHall = infoMap.getString("businessHall");
            phoneNum = infoMap.getLong("phoneNum");
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(businessHall) || !isMobile(phoneNum.toString())) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        // 订单信息
        List<JSONObject> list = (List<JSONObject>) jsonObject.get("orders");
        // 创建结果集
        List<Order> orderList = new ArrayList<>();
        // 创建参数
        Date date = new Date();
        String orderGroup = UUID.randomUUID().toString();
        Order order = null;
        GoodsInfo goodsInfo = null;
        GoodsModel goodsModel = null;
        // 将 List<JSONObject> 封装成 List<Order>
        for (JSONObject orderMap : list) {
            order = new Order();
            goodsInfo = new GoodsInfo();
            goodsModel = new GoodsModel();

            Long goodsInfoId, goodsModelId, phoneId = null;
            Integer goodsCount = null;
            try {
                goodsInfoId = orderMap.getLong("goodsInfoId");
                goodsModelId = orderMap.getLong("goodsModelId");
                phoneId = orderMap.getLong("phoneId");
                goodsCount = orderMap.getInteger("goodsCount");

                goodsInfo.setId(goodsInfoId);
                goodsModel.setId(goodsModelId);

                order.setAddress(address);
                order.setBusinessHall(businessHall);
                order.setCreateTime(date);
                order.setGoodsCount(goodsCount);
                order.setGoodsInfo(goodsInfo);
                order.setGoodsModel(goodsModel);
                order.setOrderGroup(orderGroup);
                order.setPhoneId(phoneId);
                order.setPhoneNum(phoneNum);
                order.setUsername(username);

                orderList.add(order);
            } catch (Exception e) {
                return null;
            }
        }
        return orderList;
    }

    /**
     * <span>根据phoneId和orderGroup删除订单</span>
     * 
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    @Test
    @Transactional
    public void test_deleteByPhoneIdAndOrderGroup() {
        String aa = "{\"orders\":[{\"goodsModelId\":\"6\",\"goodsCount\":\"100\",\"orderGroup\":\"456789\"},"
                + "{\"goodsModelId\":\"3\",\"goodsCount\":\"100\",\"orderGroup\":\"456789\"}]}";
        JSONObject jsonObject = JSON.parseObject(aa);
        Object object = jsonObject.get("orders");

        // 参数校验
        List<JSONObject> list = parseJsonToListForOrder(object);

        // 根据orderGroup批量删除
        int i = orderRepository.deleteByOrderGroup(list.get(0).getString("orderGroup"));
        System.out.println("删除了： " + i);

        // 根据 goodsModelId 找到对应的产品型号，在库存上加 goodsCount
        for (JSONObject map : list) {
            int count = goodsModelRepository.updateGoodsCountById(map.getLong("goodsModelId"), map.getInteger("goodsCount"));
            System.out.println("修改了： " + count);
        }

    }

    private List<ShoppingCart> getCarts() {
        ShoppingCart cart1 = new ShoppingCart();
        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setId(666l);
        GoodsModel goodsModel = new GoodsModel();
        goodsModel.setId(666l);
        cart1.setGoodsCount(666);
        cart1.setPhoneId(18752334499l);
        cart1.setGoodsInfo(goodsInfo);
        cart1.setGoodsModel(goodsModel);

        ShoppingCart cart2 = new ShoppingCart();
        GoodsInfo goodsInfo2 = new GoodsInfo();
        goodsInfo2.setId(777l);
        GoodsModel goodsModel2 = new GoodsModel();
        goodsModel2.setId(777l);
        cart2.setGoodsCount(777);
        cart2.setPhoneId(18752334455l);
        cart2.setGoodsInfo(goodsInfo2);
        cart2.setGoodsModel(goodsModel2);

        List<ShoppingCart> list = new ArrayList<>();
        list.add(cart1);
        list.add(cart2);

        return list;
    }

    /**
     * <li>将 List\<map\> 对象中的键值对放入 ShoppingCart</li>
     *
     * @Author: yucong
     * @Since: 2019年3月27日
     */
    @SuppressWarnings("unchecked")
    private List<ShoppingCart> parseJsonToListForCart(Object object) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<ShoppingCart> result = new ArrayList<>();
        ShoppingCart cart = null;
        GoodsInfo info = null;
        GoodsModel model = null;
        for (Map<String, Object> map : list) {
            map = paramJudge(map);
            if (CollectionUtils.isEmpty(map)) {
                return null;
            }
            cart = new ShoppingCart();
            info = new GoodsInfo();
            model = new GoodsModel();

            info.setId(Long.parseLong(String.valueOf(map.get("goodsInfoId"))));
            model.setId(Long.parseLong(String.valueOf(map.get("goodsModelId"))));
            cart.setGoodsCount(Integer.parseInt(String.valueOf(map.get("goodsCount"))));
            cart.setPhoneId(Long.parseLong(String.valueOf(map.get("phoneId"))));
            cart.setGoodsInfo(info);
            cart.setGoodsModel(model);

            result.add(cart);
        }
        return result;
    }

    /**
     * <li>将 List<map> 对象中的键值对放入 GoodsModel</li>
     *
     * @Author: yucong
     * @Since: 2019年3月27日
     */
    @SuppressWarnings("unchecked")
    private List<JSONObject> parseJsonToListForOrder(Object object) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<JSONObject> result = new ArrayList<>();
        JSONObject tem = null;
        try {
            for (Map<String, Object> map : list) {
                tem = new JSONObject();
                String orderGroup = String.valueOf(map.get("orderGroup"));
                if (StringUtils.isEmpty(orderGroup)) {
                    return null;
                }
                tem.put("orderGroup", orderGroup);
                tem.put("goodsCount", Integer.parseInt(String.valueOf(map.get("goodsCount"))));
                tem.put("goodsModelId", Long.parseLong(String.valueOf(map.get("goodsModelId"))));
                result.add(tem);
            }
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    /**
     * <li>判断map集合中的参数是否存在并且是否能转换成功</li>
     *
     * @Author: yucong
     * @Since: 2019年3月27日
     */
    private Map<String, Object> paramJudge(Map<String, Object> map) {
        try {
            Long.parseLong(String.valueOf(map.get("phoneId")));
            Integer.parseInt(String.valueOf(map.get("goodsCount")));
            Long.parseLong(String.valueOf(map.get("goodsInfoId")));
            Long.parseLong(String.valueOf(map.get("goodsModelId")));
        } catch (Exception e) {
            return null;
        }
        return map;
    }

    private List<Order> getList() {
        String uuid = String.valueOf(UUID.randomUUID().getMostSignificantBits());

        Order order = new Order();
        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setId(1l);
        GoodsModel goodsModel = new GoodsModel();
        goodsModel.setId(2l);
        order.setAddress("nanjing pukou");
        order.setBusinessHall("the business hall of pukou");
        order.setCreateTime(new Date());
        order.setGoodsCount(2);
        order.setGoodsInfo(goodsInfo);
        order.setGoodsModel(goodsModel);
        order.setOrderGroup(uuid);
        order.setPhoneId(18752334498l);
        order.setPhoneNum(18752334498l);
        order.setUsername("Jack");

        Order order1 = new Order();
        GoodsInfo goodsInfo1 = new GoodsInfo();
        goodsInfo1.setId(1l);
        GoodsModel goodsModel1 = new GoodsModel();
        goodsModel1.setId(2l);
        order1.setAddress("huaian lianshui");
        order1.setBusinessHall("the business hall of lianshui");
        order1.setCreateTime(new Date());
        order1.setGoodsCount(2);
        order1.setGoodsInfo(goodsInfo1);
        order1.setGoodsModel(goodsModel1);
        order1.setOrderGroup(uuid);
        order1.setPhoneId(18752334498l);
        order1.setPhoneNum(18752334498l);
        order1.setUsername("Rose");

        List<Order> list = new ArrayList<>();
        list.add(order);
        list.add(order1);
        return list;
    }

    private List<Object> dataProcess(List<Map<String, Object>> list) {
        List<Object> data = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return data;
        }

        // 获取产品名称
        List<String> goodsNames = new ArrayList<>();
        for (Map<String, Object> map : list) {
            String goods_name = String.valueOf(map.get("goods_name"));
            if (!goodsNames.contains(goods_name)) {
                goodsNames.add(goods_name);
            }
        }
        // 过滤list集合中同一产品下的不同品牌，默认选择第一个
        List<Map<String, Object>> removeSameModel = new ArrayList<>();
        for (String goodsName : goodsNames) {
            for (Map<String, Object> map : list) {
                if (goodsName.equals(map.get("goods_name"))) {
                    removeSameModel.add(map);// 默认选择第一个
                    break;
                }
            }
        }

        // 获取类型名称
        List<String> goodsTypes = new ArrayList<>();
        for (Map<String, Object> map : list) {
            String goods_type = String.valueOf(map.get("goods_type"));
            if (!goodsTypes.contains(goods_type)) {
                goodsTypes.add(goods_type);
            }
        }
        // 对removeSameModel集合中的产品进行分类，分类顺序不变
        List<Map<String, Object>> groupByType = null;
        for (String goodsType : goodsTypes) {
            groupByType = new ArrayList<>();
            for (Map<String, Object> map : removeSameModel) {
                if (goodsType.equals(map.get("goods_type"))) {
                    groupByType.add(map);
                }
            }
            data.add(groupByType);
        }
        return data;
    }

    // 验证手机号
    private boolean isMobile(final String str) {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    @Test
    public void queryAllLimitCount() {
        orderService.queryAllLimitCount(1l, 1l);
    }

    @Test
    @Transactional
    public void test_deleteShoppingCartByIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(1l);
        ids.add(3l);
        int count = shoppingCartRepository.deleteShoppingCartByIds(ids);
        System.out.println("删除了：" + count);
    }
}
