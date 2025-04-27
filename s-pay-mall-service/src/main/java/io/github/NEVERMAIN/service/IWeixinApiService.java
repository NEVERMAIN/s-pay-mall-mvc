package io.github.NEVERMAIN.service;

import io.github.NEVERMAIN.domain.po.WxTemplateMessageVO;
import io.github.NEVERMAIN.domain.req.WxQrcodeCreateReq;
import io.github.NEVERMAIN.domain.res.WxAccessTokenRes;
import io.github.NEVERMAIN.domain.res.WxQrcodeCreateRes;
import org.springframework.web.bind.annotation.PostMapping;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IWeixinApiService {

    /**
     * 获取 access-token
     * https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
     */
    @GET("/cgi-bin/token")
    Call<WxAccessTokenRes> getAccessToken(
            @Query("grant_type") String grant_type,
            @Query("appid") String appid,
            @Query("secret") String secret
    );

    /**
     * 获取创建二维码需要的 ticket
     * https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN
     * @return
     */
    @POST("/cgi-bin/qrcode/create")
    Call<WxQrcodeCreateRes> createQrcode(
            @Query("access_token") String access_token,
            @Body WxQrcodeCreateReq wxQrcodeCreateReq
    );

    /**
     * 发送模版消息
     * https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN
     */
    @POST("/cgi-bin/message/template/send")
    Call<Void> sendTemplateMessage(
            @Query("access_token") String access_token,
            @Body WxTemplateMessageVO wxMessageTemplateVo
    );

}
