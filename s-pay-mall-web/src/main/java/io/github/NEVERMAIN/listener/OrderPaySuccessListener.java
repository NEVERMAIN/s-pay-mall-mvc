package io.github.NEVERMAIN.listener;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 支付成功回调消息
 */
@Slf4j
@Component
public class OrderPaySuccessListener {

    @Subscribe
    public void handleEvent(String message) {
        log.info("收到支付成功消息,接下来进行发货、充值、开会员。返利: {}", message);
    }

}
