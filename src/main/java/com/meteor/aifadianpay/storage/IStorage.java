package com.meteor.aifadianpay.storage;

import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.afdian.response.SkuDetail;
import com.meteor.aifadianpay.storage.export.ExportOrder;
import com.meteor.aifadianpay.storage.export.ExportSkuDetail;

import java.util.List;

public interface IStorage {
    /**
     * 处理订单
     */
    boolean handeOrder(Order order,boolean b);

    /**
     * 获取玩家捐赠总数
     * @param p
     * @return
     */
    int queryPlayerDonate(String p);

    boolean isHandleOrder(String tradeNo);

    /**
     * 获取订单表所有对象
     */
    List<ExportOrder> queryAllOrder();

    /**
     * 获取型号表所有对象
     */
    List<ExportSkuDetail> queryAllSkudetail();

    /**
     * 导入数据记录
     */
    void importData(List<ExportOrder> exportOrders,List<ExportSkuDetail> skuDetails);

}
