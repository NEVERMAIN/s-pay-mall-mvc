package io.github.NEVERMAIN.service.login;

import com.google.common.cache.Cache;
import io.github.NEVERMAIN.domain.vo.WxTemplateMessageVO;
import io.github.NEVERMAIN.domain.req.WxQrcodeCreateReq;
import io.github.NEVERMAIN.domain.res.WxAccessTokenRes;
import io.github.NEVERMAIN.domain.res.WxQrcodeCreateRes;
import io.github.NEVERMAIN.service.ILoginService;
import io.github.NEVERMAIN.service.IWeixinApiService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService implements ILoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    @Value("${weixin.config.appid}")
    private String appid;

    @Value("${weixin.config.secret}")
    private String secret;

    @Value("${weixin.config.template_id}")
    private String template_id;

    @Resource
    private Cache<String,String> ticketCache;

    @Resource
    private Cache<String, String> accessTokenCache;

    @Resource
    private IWeixinApiService weixinApiService;

    public LoginService(IWeixinApiService weixinApiService) {
        this.weixinApiService = weixinApiService;
    }

    @Override
    public String createTicket() throws IOException {



            String accessToken = accessTokenCache.getIfPresent(appid);
            if(accessToken == null){
                Call<WxAccessTokenRes> call = weixinApiService.getAccessToken("client_credential", appid, secret);
                WxAccessTokenRes body = call.execute().body();
                assert weixinApiService != null;
                 accessToken = body.getAccess_token();

                accessTokenCache.put(appid,accessToken);
            }

            WxQrcodeCreateReq wxQrcodeCreateReq = WxQrcodeCreateReq.builder()
                    .action_name("QR_SCENE")
                    .expired_seconds(2592000)
                    .action_info(WxQrcodeCreateReq.ActionInfo.builder()
                            .scene(WxQrcodeCreateReq.Scene.builder()
                                    .scene_id(100601)
                                    .build())
                            .build())
                    .build();

            Call<WxQrcodeCreateRes> qrcodeCall = weixinApiService.createQrcode(accessToken, wxQrcodeCreateReq);
            WxQrcodeCreateRes wxQrcodeCreateRes = qrcodeCall.execute().body();
            assert wxQrcodeCreateRes != null;
            String ticket = wxQrcodeCreateRes.getTicket();

            return ticket;

    }

    @Override
    public void bindingUserInfo(String ticket, String openid) throws IOException {
        // 绑定 ticket 和 openid
        ticketCache.put(ticket,openid);

        // 发送模版消息
        String accessToken = accessTokenCache.getIfPresent(appid);
        if (null == accessToken){
            Call<WxAccessTokenRes> call = weixinApiService.getAccessToken("client_credential", appid, secret);
            WxAccessTokenRes weixinTokenRes = call.execute().body();
            assert weixinTokenRes != null;
            accessToken = weixinTokenRes.getAccess_token();
            accessTokenCache.put(appid,accessToken);
        }


        // 2. 发送模板消息
        Map<String, Map<String, String>> data = new HashMap<>();
        WxTemplateMessageVO.put(data, WxTemplateMessageVO.TemplateKey.USER, openid);

        WxTemplateMessageVO templateMessageDTO = new WxTemplateMessageVO(openid, template_id);
        templateMessageDTO.setUrl("https://gaga.plus");
        templateMessageDTO.setData(data);

        Call<Void> call = weixinApiService.sendTemplateMessage(accessToken, templateMessageDTO);
        call.execute();

    }

    @Override
    public String checkLogin(String ticket) {
        String openid = ticketCache.getIfPresent(ticket);
        if(openid != null){
            return openid;
        }
        return null;
    }
}
