package io.github.NEVERMAIN.domain.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;
    private String userId;
    private String nickname;
    private String avatarUrl;
    private Date createTime;
    private Date updateTime;
    private Date lastLoginTime;
    private Integer status;

}
