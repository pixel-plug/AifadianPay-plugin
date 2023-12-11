package com.meteor.aifadianpay.data.job;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.AfadianApi;
import com.meteor.aifadianpay.afdian.handle.HandlerQueryOrdersResponse;
import com.meteor.aifadianpay.afdian.request.AfdOrderReq;
import com.meteor.aifadianpay.afdian.response.Orders;
import com.meteor.aifadianpay.afdian.response.QueryOrderResponse;
import com.meteor.aifadianpay.cron.QueryOrderTask;
import com.meteor.aifadianpay.httputil.callback.AsyncHttpResponseCallBack;
import com.meteor.aifadianpay.httputil.response.PackHttpResponse;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QueryOrderJob implements Job {

    private static HandlerQueryOrdersResponse handlerQueryOrdersResponse = null;

    public static void init(HandlerQueryOrdersResponse handlerQueryOrdersResponse){
        QueryOrderJob.handlerQueryOrdersResponse  = handlerQueryOrdersResponse;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        execute(true);
    }

    /**
     * 计划任务
     * @param isSave 是否保存至数据库中
     * @throws JobExecutionException
     */
    public void execute(boolean isSave) throws JobExecutionException {

        AifadianPay.INSTANCE.getLogger().info("开始处理订单");

        AfdOrderReq afdOrderReq = new AfdOrderReq(1);
        int currentPage = 1;
        // 从第一页开始处理
        AfadianApi.afadianApi.queryOrders(afdOrderReq, new AsyncHttpResponseCallBack() {
            @Override
            public void success(PackHttpResponse packHttpResponse) {
                QueryOrderResponse queryOrderResponse = AfadianApi.afadianApi.toOrders(packHttpResponse);

                handlerQueryOrdersResponse.success(queryOrderResponse,isSave);

                Orders orders = queryOrderResponse.getOrders();
                if(orders!=null&&currentPage<orders.getTotalPage()){
                    for(int i = currentPage+1;i<=orders.getTotalPage();i++){
                        AfdOrderReq next = new AfdOrderReq(i);
                        PackHttpResponse nextResponse = AfadianApi.afadianApi.queryOrders(next);
                        QueryOrderResponse nextQueryOrderResponse = AfadianApi.afadianApi.toOrders(nextResponse);
                        handlerQueryOrdersResponse.success(nextQueryOrderResponse,isSave);
                    }
                }

            }

            @Override
            public void fail(Exception e) {

            }
        });
    }


}
