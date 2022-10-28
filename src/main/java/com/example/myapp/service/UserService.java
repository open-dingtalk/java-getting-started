package com.example.myapp.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.request.OapiV2UserGetuserinfoRequest;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.api.response.OapiV2UserGetuserinfoResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/24
 */
@Slf4j
@Service
public class UserService {
    private final AccessTokenService accessTokenService;

    @Autowired
    public UserService(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    /**
     * see <a href="https://open.dingtalk.com/document/orgapp-server/obtain-the-userid-of-a-user-by-using-the-log-free">/v1.0/oauth2/ssoUserInfo</a>
     * @param authCode temp auth code
     * @return user info
     * @throws Exception open api exception
     */
    public UserInfo getUserInfo(String authCode) throws Exception {
        try {
            DingTalkClient client = new DefaultDingTalkClient(
                    "https://oapi.dingtalk.com/topapi/v2/user/getuserinfo");
            OapiV2UserGetuserinfoRequest request = new OapiV2UserGetuserinfoRequest();
            request.setCode(authCode);

            OapiV2UserGetuserinfoResponse rsp = client.execute(request, accessTokenService.getAccessToken());
            if (!rsp.isSuccess()) {
                log.error("UserService_getUserInfo getUserInfo failed, errorCode={}, errorMessage={}.",
                        rsp.getErrcode(), rsp.getErrmsg());
                return null;
            }

            OapiV2UserGetuserinfoResponse.UserGetByCodeResponse userGetByCodeResponse = rsp.getResult();
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userGetByCodeResponse.getUserid());
            userInfo.setName(userGetByCodeResponse.getName());

            String avatar = getAvatar(userGetByCodeResponse.getUserid());
            userInfo.setAvatar(avatar);
            log.info("UserService_getUserInfo get success, userid={}, avatar={}.", userInfo.getUserId(),
                    userInfo.getAvatar());
            return userInfo;
        } catch (Exception e) {
            log.error("UserService_getUserInfo getSsoUserInfoWithOptions throw Exception", e);
            throw e;
        }
    }

    private String getAvatar(String userid) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
        OapiV2UserGetRequest req = new OapiV2UserGetRequest();
        req.setUserid(userid);
        req.setLanguage("zh_CN");

        OapiV2UserGetResponse userGetResponse = client.execute(req, accessTokenService.getAccessToken());
        if (!userGetResponse.isSuccess()) {
            log.error("UserService_getUserInfo getAvatar failed, errorCode={}, errorMessage={}.",
                    userGetResponse.getErrcode(), userGetResponse.getErrmsg());
            return null;
        }

        return userGetResponse.getResult().getAvatar();
    }
}
