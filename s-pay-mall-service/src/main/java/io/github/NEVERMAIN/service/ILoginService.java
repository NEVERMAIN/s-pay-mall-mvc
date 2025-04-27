package io.github.NEVERMAIN.service;

import java.io.IOException;

/**
 * 登录服务
 */
public interface ILoginService {

    /**
     * 创建申请二维码的 ticket
     * @return
     */
    String createTicket() throws IOException;

    /**
     * 绑定 ticket 和 openid 关系
     * @param ticket
     * @param openid
     */
    void bindingUserInfo(String ticket, String openid) throws IOException;

    /**
     * 检查是否登录
     * @param ticket
     * @return
     */
    String checkLogin(String ticket);

}
