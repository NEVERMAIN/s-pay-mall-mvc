package io.github.NEVERMAIN.domain.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxAccessTokenRes {
    /** 获取到的凭证 */
    private String access_token;
    /** 凭证有效时间，单位：秒 */
    private Integer expires_in;
    /** 错误码  */
    private Integer error_code;
    /** 错误信息 */
    private String error_msg;

}
