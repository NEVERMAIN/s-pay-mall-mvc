package io.github.NEVERMAIN.job;

import io.github.NEVERMAIN.service.IUserOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TimeoutCloseOrderJob {

    private IUserOrderService userOrderService;

    public TimeoutCloseOrderJob(IUserOrderService userOrderService) {
        this.userOrderService = userOrderService;
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        try {
            log.info("任务: 超时30分钟订单关闭....");
            List<String> userOrderIds = userOrderService.queryTimeoutCloseOrderList();
            if (null == userOrderIds || userOrderIds.isEmpty()) {
                log.info("定时任务,超时30分钟后订单关闭。暂无超时未支付订单 orderIds is null");
                return;
            }
            for (String userOrderId : userOrderIds) {
                boolean status = userOrderService.changeOrderClose(userOrderId);
                log.info("定时任务,超时30分钟后订单关闭. userOrderId:{} status:{}", userOrderId, status);
            }
        } catch (Exception e) {
            log.error("任务: 超时30分钟订单关闭. error ", e);
        }
    }

}
