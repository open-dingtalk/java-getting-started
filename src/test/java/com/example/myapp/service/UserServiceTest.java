package com.example.myapp.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.request.OapiV2UserGetuserinfoRequest;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.api.response.OapiV2UserGetuserinfoResponse;
import com.taobao.api.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/24
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({UserService.class})
public class UserServiceTest {
    private final AccessTokenService accessTokenService = mock(AccessTokenService.class);
    private final UserService userService = new UserService(accessTokenService);
    private final String authCode = "authCode";
    private final DefaultDingTalkClient getUserInfoClient = mock(DefaultDingTalkClient.class);
    private final DefaultDingTalkClient getAvatarClient = mock(DefaultDingTalkClient.class);

    @Before
    public void setUp() throws Exception {
        whenNew(DefaultDingTalkClient.class).withArguments(
                "https://oapi.dingtalk.com/topapi/v2/user/getuserinfo").thenReturn(getUserInfoClient);
        whenNew(DefaultDingTalkClient.class).withArguments(
                "https://oapi.dingtalk.com/topapi/v2/user/get").thenReturn(getAvatarClient);
    }

    private void mockingUserByCodeSuccess() throws ApiException  {
        OapiV2UserGetuserinfoResponse response = new OapiV2UserGetuserinfoResponse();
        response.setErrcode(0L);

        OapiV2UserGetuserinfoResponse.UserGetByCodeResponse userGetByCodeResponse = new
                OapiV2UserGetuserinfoResponse.UserGetByCodeResponse();
        userGetByCodeResponse.setName("name");
        userGetByCodeResponse.setUserid("userId");
        response.setResult(userGetByCodeResponse);

        when(getUserInfoClient.execute(any(OapiV2UserGetuserinfoRequest.class), any())).
                thenReturn(response);
    }

    @Test
    public void getUserInfoReturnUserInfoIfCommon() throws Exception {
        mockingUserByCodeSuccess();

        OapiV2UserGetResponse oapiV2UserGetResponse = new OapiV2UserGetResponse();
        oapiV2UserGetResponse.setErrcode(0L);

        OapiV2UserGetResponse.UserGetResponse userGetResponse = new OapiV2UserGetResponse.UserGetResponse();
        userGetResponse.setAvatar("avatar");
        oapiV2UserGetResponse.setResult(userGetResponse);

        when(getAvatarClient.execute(any(OapiV2UserGetRequest.class), any())).thenReturn(oapiV2UserGetResponse);

        UserInfo userInfo = userService.getUserInfo(authCode);
        assertNotNull(userInfo);
        assertEquals("userId", userInfo.getUserId());
        assertEquals("name", userInfo.getName());
        assertEquals("avatar", userInfo.getAvatar());
    }

    @Test(expected = ApiException.class)
    public void getUserInfoThrowApiExceptionIfClientGetUserException() throws Exception {
        when(getUserInfoClient.execute(any(OapiV2UserGetuserinfoRequest.class), any())).thenThrow(new ApiException());
        userService.getUserInfo(authCode);
    }

    @Test(expected = ApiException.class)
    public void getUserInfoThrowExceptionIfClientGetAvatarException() throws Exception {
        mockingUserByCodeSuccess();

        when(getAvatarClient.execute(any(OapiV2UserGetRequest.class), any())).thenThrow(new ApiException());
        userService.getUserInfo(authCode);
    }

    @Test
    public void getUserInfoReturnNullIfClientGetUserFailed() throws Exception {
        OapiV2UserGetuserinfoResponse response = new OapiV2UserGetuserinfoResponse();
        response.setErrcode(1L);
        when(getUserInfoClient.execute(any(OapiV2UserGetuserinfoRequest.class), any())).
                thenReturn(response);
        UserInfo messageId = userService.getUserInfo(authCode);
        assertNull(messageId);
    }

    @Test
    public void getUserInfoReturnAvatarNullIfClientAvatarReturnFailed() throws Exception {
        mockingUserByCodeSuccess();

        OapiV2UserGetResponse oapiV2UserGetResponse = new OapiV2UserGetResponse();
        oapiV2UserGetResponse.setErrcode(1L);

        when(getAvatarClient.execute(any(OapiV2UserGetRequest.class), any())).thenReturn(oapiV2UserGetResponse);
        UserInfo userInfo = userService.getUserInfo(authCode);

        assertNotNull(userInfo);
        assertEquals("userId", userInfo.getUserId());
        assertEquals("name", userInfo.getName());
        assertNull(userInfo.getAvatar());
    }
}