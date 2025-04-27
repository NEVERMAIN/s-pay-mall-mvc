package io.github.NEVERMAIN;

import io.github.NEVERMAIN.domain.req.WxQrcodeCreateReq;
import io.github.NEVERMAIN.domain.res.WxAccessTokenRes;
import io.github.NEVERMAIN.domain.res.WxQrcodeCreateRes;
import io.github.NEVERMAIN.service.IWeixinApiService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Call;

@SpringBootTest
public class WxApiServiceTest {

    private static final Logger log = LoggerFactory.getLogger(WxApiServiceTest.class);
    @Value("${weixin.config.appid}")
    private String appid;

    @Value("${weixin.config.secret}")
    private String secret;

    @Resource
    private IWeixinApiService weixinApiService;

    @Test
    public void testGetAccessToken() {

        try{
            Call<WxAccessTokenRes> call = weixinApiService.getAccessToken("client_credential", appid, secret);
            WxAccessTokenRes body = call.execute().body();
            log.info("获取accessToken成功, {}",  body.getAccess_token());
        }catch (Exception e){
            log.error("",e);
        }
    }

    @Test
    public void testCreateQrcode() {

        try{
            Call<WxAccessTokenRes> call = weixinApiService.getAccessToken("client_credential", appid, secret);
            WxAccessTokenRes body = call.execute().body();
            String accessToken = body.getAccess_token();

            log.info("获取 accessToken 成功, {}",  body.getAccess_token());

            WxQrcodeCreateReq wxQrcodeCreateReq = WxQrcodeCreateReq.builder()
                    .action_name("QR_SCENE")
                    .expired_seconds(604800)
                    .action_info(WxQrcodeCreateReq.ActionInfo.builder()
                            .scene(WxQrcodeCreateReq.Scene.builder()
                                    .scene_id(123)
                                    .build())
                            .build())
                    .build();

            Call<WxQrcodeCreateRes> qrcodeCall = weixinApiService.createQrcode(accessToken, wxQrcodeCreateReq);
            WxQrcodeCreateRes wxQrcodeCreateRes = qrcodeCall.execute().body();
            log.info("创建 ticket,申请 ticket 成功: {}",wxQrcodeCreateRes.getTicket());

        }catch (Exception e){
            log.error("",e);
        }

    }



}
