package com.example.myapp.service;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/24
 */
@Getter
@Setter
@ToString
public class UserInfo {
    private String name;

    @JSONField(name = "userid")
    private String userId;

    private String avatar;

}
