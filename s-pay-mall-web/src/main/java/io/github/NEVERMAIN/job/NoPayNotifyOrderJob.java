package io.github.NEVERMAIN.job;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import io.github.NEVERMAIN.service.IUserOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NoPayNotifyOrderJob {

    private IUserOrderService userOrderService;

    private AlipayClient alipayClient;

    public NoPayNotifyOrderJob(IUserOrderService userOrderService, AlipayClient alipayClient) {
        this.userOrderService = userOrderService;
        this.alipayClient = alipayClient;
    }

    @Scheduled(cron = "0/3 * * * * ?")
    public void exec(){
        try{
            log.info("任务: 检测未接收到或未正确处理的支付回调通知");
            List<String> userOrderIds = userOrderService.queryNoPayNotifyOrder();
            if (null == userOrderIds || userOrderIds.isEmpty()) return;

            for (String userOrderId : userOrderIds) {
                AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
                bizModel.setOutTradeNo(userOrderId);
                request.setBizModel(bizModel);

                AlipayTradeQueryResponse alipayTradeQueryResponse = alipayClient.execute(request);
                String code = alipayTradeQueryResponse.getCode();
                // 判断状态码
                if ("10000".equals(code)) {
                    String tradeNo = alipayTradeQueryResponse.getTradeNo();
                    userOrderService.changeOrderPaySuccess(userOrderId,  tradeNo, alipayTradeQueryResponse.getSendPayDate());
                }
            }
        }catch (Exception e){
            log.error("检测未接收到或未正确处理的支付回调通知失败", e);
        }



    }


}
