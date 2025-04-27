package io.github.NEVERMAIN.domain.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WxQrcodeCreateReq {

    /**
     * 二维码类型
     */
    private String action_name;
    /**
     * 二维码有效期
     */
    private Integer expired_seconds;
    /**
     * 二维码详细信息
     */
    private ActionInfo action_info;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActionInfo{
        private Scene scene;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Scene{
        /**
         * 场景值ID
         */
        private Integer scene_id;
        /**
         * 场景值ID
         */
        private String scene_str;
    }


}
