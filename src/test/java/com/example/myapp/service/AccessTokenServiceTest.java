package com.example.myapp.service;

import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponseBody;
import com.aliyun.tea.TeaException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/20
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AccessTokenService.class})
public class AccessTokenServiceTest {
    private final AccessTokenService accessTokenService = new AccessTokenService();
    private final Client auth2Client = mock(Client.class);

    @Before
    public void setUp() throws Exception {
        Whitebox.setInternalState(accessTokenService, "appKey", "ak");
        Whitebox.setInternalState(accessTokenService, "appSecret", "sk");
        whenNew(Client.class).withAnyArguments().thenReturn(auth2Client);
        Whitebox.setInternalState(accessTokenService, "auth2Client", auth2Client);
    }

    private AccessTokenService.AccessToken getAccessToken() {
        return Whitebox.getInternalState(accessTokenService, "accessToken");
    }

    private void setAccessToken(String token, Long expireTimestamp) {
        AccessTokenService.AccessToken oldAccessToken = new AccessTokenService.AccessToken();
        oldAccessToken.setAccessToken(token);
        oldAccessToken.setExpireTimestamp(expireTimestamp);

        Whitebox.setInternalState(accessTokenService, "accessToken", oldAccessToken);
    }

    private void mockingServiceReturnToken(String token, Long expiredIn) throws Exception {
        GetAccessTokenResponseBody accessTokenResponseBody = new GetAccessTokenResponseBody();
        accessTokenResponseBody.setAccessToken(token);
        accessTokenResponseBody.setExpireIn(expiredIn);

        GetAccessTokenResponse accessTokenResponse = new GetAccessTokenResponse();
        accessTokenResponse.setBody(accessTokenResponseBody);

        when(auth2Client.getAccessToken(any())).thenReturn(accessTokenResponse);
    }

    @Test
    public void initGetTokenSuccessIfTokenServiceGetSuccess() throws Exception {
        mockingServiceReturnToken("ak", 1000L);

        accessTokenService.init();
        verify(auth2Client).getAccessToken(any());

        AccessTokenService.AccessToken currentAccessToken = getAccessToken();
        assertTrue(currentAccessToken.getExpireTimestamp() > System.currentTimeMillis());
        assertEquals("ak", currentAccessToken.getAccessToken());
    }

    @Test(expected = RuntimeException.class)
    public void initThrowExceptionIfAppKeyNull() throws Exception {
        Whitebox.setInternalState(accessTokenService, "appKey", (Object) null);
        accessTokenService.init();
    }

    @Test(expected = RuntimeException.class)
    public void initThrowExceptionIfAppSecretNull() throws Exception {
        Whitebox.setInternalState(accessTokenService, "appSecret", (Object) null);
        accessTokenService.init();
    }

    @Test(expected = RuntimeException.class)
    public void initThrowExceptionIfGetTokenServiceErrorThreeTimes() throws Exception {
        GetAccessTokenResponseBody missingExpiredInBody = new GetAccessTokenResponseBody();
        missingExpiredInBody.setAccessToken("ak");

        GetAccessTokenResponse missingExpiredIn = new GetAccessTokenResponse();
        missingExpiredIn.setBody(missingExpiredInBody);

        when(auth2Client.getAccessToken(any())).thenReturn(null,
                new GetAccessTokenResponse(), missingExpiredIn);
        accessTokenService.init();
    }

    @Test(expected = Exception.class)
    public void initThrowExceptionIfGetTokenServiceThrowException() throws Exception {
        when(auth2Client.getAccessToken(any())).thenThrow(new Exception());
        accessTokenService.init();
    }

    @Test(expected = RuntimeException.class)
    public void initThrowRunTimeExceptionIfGetTokenServiceThrowTeaException() throws Exception {
        when(auth2Client.getAccessToken(any())).thenThrow(new TeaException());
        accessTokenService.init();

        verify(auth2Client, times(3)).getAccessToken(any());
    }

    @Test
    public void checkAccessTokenWillUpdateAccessTokenIfTokenExpired() throws Exception {
        setAccessToken("oldToken", 1L);

        mockingServiceReturnToken("newAccessToken", 2*60*60L);
        accessTokenService.checkAccessToken();

        AccessTokenService.AccessToken currentAccessToken = getAccessToken();
        assertTrue(currentAccessToken.getExpireTimestamp() > System.currentTimeMillis());
        assertEquals("newAccessToken", currentAccessToken.getAccessToken());
    }

    @Test
    public void checkAccessTokenNotUpdateAccessTokenIfGetTokenServiceThrowException() throws Exception {
        setAccessToken("accessToken", 1L);

        when(auth2Client.getAccessToken(any())).thenThrow(new TeaException());
        accessTokenService.checkAccessToken();

        AccessTokenService.AccessToken currentAccessToken = getAccessToken();
        assertEquals(new Long(1L), currentAccessToken.getExpireTimestamp());
        assertEquals("accessToken", accessTokenService.getAccessToken());
    }

    @Test
    public void checkAccessTokenNotUpdateAccessTokenIfNotExpired() throws Exception {
        Long expiredTimestamp = System.currentTimeMillis() + 60 * 60 * 1000L;
        setAccessToken("accessToken", expiredTimestamp);

        when(auth2Client.getAccessToken(any())).thenThrow(new TeaException());
        accessTokenService.checkAccessToken();

        AccessTokenService.AccessToken currentAccessToken = getAccessToken();
        assertEquals(expiredTimestamp, currentAccessToken.getExpireTimestamp());
        assertEquals("accessToken", currentAccessToken.getAccessToken());

        verify(auth2Client, never()).getAccessToken(any());
    }

    @Test
    public void checkAccessTokenNotUpdateAccessTokenIfTokenIsNotExist() throws Exception {
        when(auth2Client.getAccessToken(any())).thenThrow(new TeaException());
        accessTokenService.checkAccessToken();
        verify(auth2Client, never()).getAccessToken(any());
    }

    @Test
    public void isTokenNearlyExpiredFalseIfNotExpired() {
        Long expiredTimestamp = System.currentTimeMillis() + 60 * 60 * 1000L;
        setAccessToken("accessToken", expiredTimestamp);
        boolean isExpired = accessTokenService.isTokenNearlyExpired();
        assertFalse(isExpired);
    }
}