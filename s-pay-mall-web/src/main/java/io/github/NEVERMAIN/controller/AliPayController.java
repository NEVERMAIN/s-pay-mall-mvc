package io.github.NEVERMAIN.controller;

import com.alipay.api.internal.util.AlipaySignature;
import io.github.NEVERMAIN.common.constants.Constants;
import io.github.NEVERMAIN.common.response.Response;
import io.github.NEVERMAIN.controller.dto.CreatePayRequestDTO;
import io.github.NEVERMAIN.domain.req.ShopCartItem;
import io.github.NEVERMAIN.domain.req.ShopCartReq;
import io.github.NEVERMAIN.domain.res.UserOrderRes;
import io.github.NEVERMAIN.service.IUserOrderService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/alipay")
public class AliPayController {

    @Value("${alipay.alipay_public_key}")
    private String alipayPublicKey;
    @Resource
    private IUserOrderService userOrderService;

    @RequestMapping(value = "create_pay_order", method = RequestMethod.POST)
    public Response<String> createPayOrder(@RequestBody CreatePayRequestDTO createPayRequestDTO) {
        try {
            log.info("商品下单，根据商品ID创建支付单开始 userId:{} productList:{}", createPayRequestDTO.getUserId(),
                    createPayRequestDTO.getShopCartItemList());
            String userId = createPayRequestDTO.getUserId();
            List<ShopCartItem> shopCartItemList = createPayRequestDTO.getShopCartItemList();
            // 下单
            UserOrderRes userOrderRes = userOrderService.createOrder(ShopCartReq.builder()
                    .userId(userId)
                    .shopCartItems(shopCartItemList)
                    .build());

            log.info("商品下单，根据商品ID创建支付单完成 userId:{}, orderId:{}", userId, userOrderRes.getOrderId());
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(userOrderRes.getPayUrl())
                    .build();

        } catch (Exception e) {
            log.error("商品下单，根据商品ID创建支付单失败 userId:{} productId:{}", createPayRequestDTO.getUserId(),
                    createPayRequestDTO.getUserId(), e);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }


    @RequestMapping(value = "pay_notify", method = RequestMethod.POST)
    public String payNotify(HttpServletRequest request) {

        try {
            log.info("支付回调，消息接收 {}", request.getParameter("trade_status"));
            if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
                Map<String, String> params = new HashMap<>();
                Map<String, String[]> requestParameters = request.getParameterMap();
                for (String name : requestParameters.keySet()) {
                    params.put(name, request.getParameter(name));
                }

                String tradeNo = params.get("out_trade_no");
                String gmtPayment = params.get("gmt_payment");
                String alipayTradeNo = params.get("trade_no");

                String sign = params.get("sign");
                String content = AlipaySignature.getSignCheckContentV1(params);
                boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayPublicKey, "UTF-8"); // 验证签名

                // 支付宝验签
                if (checkSignature) {
                    // 验签通过
                    log.info("支付回调，交易名称: {}", params.get("subject"));
                    log.info("支付回调，交易状态: {}", params.get("trade_status"));
                    log.info("支付回调，支付宝交易凭证号: {}", params.get("trade_no"));
                    log.info("支付回调，商户订单号: {}", params.get("out_trade_no"));
                    log.info("支付回调，交易金额: {}", params.get("total_amount"));
                    log.info("支付回调，买家在支付宝唯一id: {}", params.get("buyer_id"));
                    log.info("支付回调，买家付款时间: {}", params.get("gmt_payment"));
                    log.info("支付回调，买家付款金额: {}", params.get("buyer_pay_amount"));
                    log.info("支付回调，支付回调，更新订单 {}", tradeNo);
                    // 更新订单未已支付

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    LocalDateTime date = LocalDateTime.parse(gmtPayment, formatter);
                    Date payTime = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());

                    userOrderService.changeOrderPaySuccess(tradeNo, alipayTradeNo, payTime);
                }

            }
            return "success";

        } catch (Exception e) {
            log.error("支付回调，处理失败", e);
            return "false";
        }

    }

}
