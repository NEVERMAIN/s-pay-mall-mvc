package io.github.NEVERMAIN.controller;

import io.github.NEVERMAIN.common.weixin.MessageTextEntity;
import io.github.NEVERMAIN.common.weixin.SignatureUtil;
import io.github.NEVERMAIN.common.weixin.XmlUtil;
import io.github.NEVERMAIN.service.ILoginService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/weixin/protal/")
public class WeixinPortalController {

    @Value("${weixin.config.originalid}")
    private String originalid;

    @Value("${weixin.config.token}")
    private String token;

    private ILoginService loginService;

    public WeixinPortalController(ILoginService loginService) {
        this.loginService = loginService;
    }


    @GetMapping(value = "receive", produces = "text/plain;charset=utf-8")
    public String validate(@RequestParam(value = "signature", required = false) String signature,
                           @RequestParam(value = "timestamp", required = false) String timestamp,
                           @RequestParam(value = "nonce", required = false) String nonce,
                           @RequestParam(value = "echostr", required = false) String echostr) {

        try {
            log.info("微信公众号验签开始: {} {} {} {} ", signature, timestamp, nonce, echostr);
            if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
                return null;
            }
            boolean check = SignatureUtil.check(token, signature, timestamp, nonce);
            log.info("微信公众号验签结束: {}", check);
            if (!check) {
                return null;
            }
            return echostr;

        } catch (Exception e) {
            log.error("微信公众号验签失败: ", e);
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(value = "receive", produces = "application/xml; charset=UTF-8")
    public String post(@RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature) {

        try {
            log.info("微信公众号接受消息开始: {} {}", requestBody, openid);
            // 消息转换
            MessageTextEntity message = XmlUtil.xmlToBean(requestBody, MessageTextEntity.class);

            if ("event".equals(message.getMsgType()) && "SCAN".equals(message.getEvent())) {
                loginService.bindingUserInfo(message.getTicket(), openid);
                return buildMessageTextEntity(openid, "登录成功");
            }

            return buildMessageTextEntity(openid, "Hello are you " + message.getContent());
        } catch (Exception e) {
            log.error("微信公众号接收消息失败: ", e);
            return "";
        }

    }

    private String buildMessageTextEntity(String openid, String content) {
        MessageTextEntity res = new MessageTextEntity();
        // 公众号分配的ID
        res.setFromUserName(originalid);
        res.setToUserName(openid);
        res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
        res.setMsgType("text");
        res.setContent(content);
        return XmlUtil.beanToXml(res);
    }


}
