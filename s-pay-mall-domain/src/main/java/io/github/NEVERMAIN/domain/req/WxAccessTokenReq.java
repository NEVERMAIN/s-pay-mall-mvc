package io.github.NEVERMAIN.domain.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxAccessTokenReq {
    /** 获取access_token填写client_credential */
    private String grant_type;
    /** 第三方用户唯一凭证 */
    private String  appid;
    /** 第三方用户唯一凭证密钥，即appsecret  */
    private String  secret;

}
