package com.yucong.insidebuy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yucong.App;
import com.yucong.insidebuy.entity.Activity;
import com.yucong.insidebuy.entity.GoodsInfo;
import com.yucong.insidebuy.entity.GoodsModel;
import com.yucong.insidebuy.entity.GoodsType;
import com.yucong.insidebuy.repository.ActivityRepository;
import com.yucong.insidebuy.repository.GoodsInfoRepository;
import com.yucong.insidebuy.repository.GoodsModelRepository;
import com.yucong.insidebuy.repository.OrderRepository;
import com.yucong.insidebuy.repository.ShoppingCartRepository;
import com.yucong.insidebuy.repository.TypeRepository;
import com.yucong.insidebuy.service.GoodsInfoService;
import com.yucong.insidebuy.service.OrderService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class TestInsideManager {

    @Autowired
    private GoodsInfoRepository goodsInfoRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private GoodsInfoService goodsInfoService;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private GoodsModelRepository goodsModelRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    public void test_finfAllForManager() {
        Page<GoodsInfo> page = goodsInfoRepository.findAllGoodsInfo(PageRequest.of(0, 2));

        JSONObject result = new JSONObject();
        result.put("data", page.getContent());
        result.put("nums", page.getTotalPages());
        System.out.println(JSONObject.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
    }

    @Test
    public void test_saveGoodsInfoForManager() {
        goodsInfoRepository.save(UtilForInside.getGoodsInfo());
    }

    @Test
    @Transactional
    @Rollback(false)
    public void test_updateGoodsForManager() {
        GoodsInfo goodsInfo = UtilForInside.getGoodsInfo();
        goodsInfo.setId(52l);
        goodsInfo.setGoodsNum(100l);
        goodsInfo.setLimitCount(100);

        goodsInfoRepository.save(goodsInfo);
    }

    @Test
    public void test_getGoodsInfoForManager() {
        GoodsInfo goodsInfo = goodsInfoRepository.findById(1l).get();
        // System.out.println(JSON.toJSONString(goodsInfo, SerializerFeature.UseISO8601DateFormat));
        System.out.println(JSON.toJSONString(goodsInfo));
    }

    @Test
    @Transactional
    public void test_deleteGoodsForManager() {
        List<Long> ids = new ArrayList<>();
        ids.add(10l);
        int count = orderRepository.findAllByGoodsInfoId(10l);
        System.out.println(count);
        if (count > 0) {
            System.out.println("客户有订单，不能删");
        } else {
            goodsInfoRepository.deleteById(10l);
            System.out.println("商品删除成功");
        }
        int num = goodsModelRepository.deleteGoodsModelByGoodsInfoId(10l);
        System.out.println("删除了型号个数： " + num);
    }

    @Test
    public void test_finfAllTypeForManager() {
        Page<GoodsType> page = typeRepository.findAll(PageRequest.of(0, 2, Direction.ASC, "typeOrder"));

        JSONObject result = new JSONObject();
        result.put("data", page.getContent());
        result.put("nums", page.getTotalPages());
        System.out.println(result.toString());
    }

    @Test
    public void test_getActivityDetail() {

        // 活动查询
        List<Activity> list = activityRepository.findAll();
        List<Map<String, Object>> buyLimit = goodsInfoService.findModelNumAndLimitCount();

        // 结果集封装
        JSONObject jsonObject = new JSONObject();
        if (CollectionUtils.isEmpty(list)) {
            jsonObject.put("activity", "");
        } else {
            jsonObject.put("activity", list.get(0));
        }
        jsonObject.put("buyLimit", buyLimit);

        System.out.println(jsonObject.toString());
    }

    @Test
    public void updateActivity() {

        String aa =
                "{\"buyLimit\":[{\"goods_info_id\":1,\"model_num\":\"aaa\",\"limit_count\":8},{\"goods_info_id\":2,\"model_num\":\"bbb\",\"limit_count\":9}],"
                        + "\"activity\":{\"endIntroduce\":\"Thank for your money!\",\"endTime\":1553506881000,\"limitTotal\":10,\"startIntroduce\":\"Activity now is starting!\",\"startTime\":1555062088000}}";

        Activity activity = checkParamForAcivity(aa);
        activity.setLimitTotal(20);
        Activity a = activityRepository.save(activity);
        System.out.println(JSON.toJSONString(a));

    }

    @Test
    public void test_changeBuyLimit() {
        GoodsInfo goodsInfo = goodsInfoRepository.findByModelNum("uuu");
        if (goodsInfo == null) {
            System.out.println("机型编码不存在 ");
        }
        goodsInfo.setLimitCount(333);

        // 更新成功返回整个对象
        GoodsInfo save = goodsInfoRepository.save(goodsInfo);

        System.out.println(JSON.toJSONString(save));
    }

    @Test
    public void test_getOrderList() {
        // 方法一
        JSONObject jsonObject = orderService.findOrderListForManager();
        System.out.println(JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat));
    }

    @Test
    public void test_deleteAll() {
        activityRepository.deleteAll();
    }

    @Test
    public void test_saveModelForManager() {
        GoodsModel model = new GoodsModel();
        model.setDiscountPrice(666d);
        model.setMarketPrice(888d);
        model.setGoodsModel("xiaomi9_yellow");
        model.setInventory(33);

        GoodsModel save = goodsModelRepository.save(model);

        System.out.println(JSON.toJSONString(save));

        // Optional<GoodsModel> goodsModel = goodsModelRepository.findById(1l);
        // System.out.println(JSON.toJSONString(goodsModel.get()));
    }

    @Test
    public void test_deleteModelForManager() {
        goodsModelRepository.deleteById(10l);

    }

    @Test
    public void test_findAllForDropDown() {
        List<GoodsType> dropDown = typeRepository.findAllForDropDown();
        System.out.println(JSON.toJSONString(dropDown));
    }

    /**
     * <li>对 GoodsType 中的参数做校验</li>
     * 
     * @param goodsType
     * @return
     */
    private Activity checkParamForAcivity(String request_body) {
        Activity a;
        try {
            JSONObject jsonObject = JSONObject.parseObject(request_body);
            a = JSON.parseObject(jsonObject.getString("activity"), Activity.class);
        } catch (Exception e) {
            return null;
        }

        // 4个非空参数
        if (StringUtils.isEmpty(a.getStartTime()) || StringUtils.isEmpty(a.getEndTime()) || StringUtils.isEmpty(a.getStartIntroduce())
                || StringUtils.isEmpty(a.getEndIntroduce())) {
            return null;
        }

        // 限购总数

        // 新增操作不设置ID，更新操作默认有ID
        return a;
    }

    @Test
    public void test_newGoodsInfoSaveForManager() {
        String aa =
                "{\"goodsInfo\":{\"id\":\"111\",\"brand\":\"honor\",\"description\":\"bigger than bigger\",\"goodsName\":\"honor10\",\"goodsNum\":1,\"goodsType\":{\"id\":1},\"modelNum\":\"vvvv\",\"picture\":\"/aa/ss/magic.png\",\"status\":1},\"goodsModels\":[{\"discountPrice\":2000.0,\"goodsInfoId\":1,\"goodsModel\":\"2\",\"id\":1,\"inventory\":12,\"marketPrice\":3000.0},{\"discountPrice\":1000.0,\"goodsInfoId\":1,\"goodsModel\":\"3\",\"id\":2,\"inventory\":34,\"marketPrice\":2000.0}]}";
        JSONObject jsonObject = JSONObject.parseObject(aa);
        GoodsInfo goodsInfo = JSONObject.parseObject(jsonObject.getString("goodsInfo"), GoodsInfo.class);
        System.out.println(JSON.toJSONString(goodsInfo, SerializerFeature.PrettyFormat));

        List<GoodsModel> list = JSONArray.parseArray(jsonObject.getString("goodsModels"), GoodsModel.class);
        System.out.println(JSON.toJSONString(list, SerializerFeature.PrettyFormat));
    }

    @Test
    public void test_parse() {
        String aa =
                "{\"brand\":\"huawei\",\"description\":\"hehe\",\"goodsName\":\"mate10\",\"goodsNum\":3,\"goodsType\":{\"id\":1},\"modelNum\":\"rrrr\",\"picture\":\"/aa/ss/mate10.png\",\"status\":1,\"updateTime\":1554720842146}";
        GoodsInfo goodsInfo = JSONObject.parseObject(aa, GoodsInfo.class);
        GoodsInfo info = new GoodsInfo();
        BeanUtils.copyProperties(goodsInfo, info);
        System.out.println(JSON.toJSONString(info, SerializerFeature.PrettyFormat));
    }

    @Test
    @Transactional
    public void test_updateActivity() {
        goodsInfoRepository.updateById(1l, 99, "aaa");
    }

    @Test
    public void test_new_getOrderList() {
        orderService.test_new_getOrderList();
    }

    @Test
    @Transactional
    public void test_modifyLimitCountToNull() {
        int count = goodsInfoRepository.modifyLimitCountToNull();
        System.out.println("修改了：" + count);
    }

    @Test
    public void test_findByids() {
        List<Long> asList = new ArrayList<>();
        asList.add(1l);
        asList.add(2l);
        asList.add(3l);
        List<GoodsInfo> list = goodsInfoRepository.findByids(asList);
        System.out.println(JSON.toJSONString(list, SerializerFeature.PrettyFormat));
    }

    @Test
    public void test_findByidRange() {
        int count = goodsInfoRepository.findByidRange(4l);
        System.out.println(count);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void test_deleteShoppingCartByGoodsInfoId() {
        int count = shoppingCartRepository.deleteShoppingCartByGoodsInfoId(1l);
        System.out.println(count);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void test_deleteOrderByGoodsInfoId() {
        int count = orderRepository.deleteOrderByGoodsInfoId(1l);
        System.out.println(count);
    }

    @Test
    public void test_findGoodsInfoIdsBygoodsTypeId() {
        List<Long> ids = goodsInfoRepository.findGoodsInfoIdsBygoodsTypeId(2l);
        System.out.println(ids);
    }

    @Test
    public void test_judgeStatusByGoodsInfoId() {
        GoodsInfo goodsInfo = goodsInfoRepository.judgeStatusByGoodsInfoId(3l);
        if (goodsInfo == null) {
            System.out.println("aaaaa");
        } else {
            System.out.println(JSON.toJSONString(goodsInfo));
        }
    }


}
