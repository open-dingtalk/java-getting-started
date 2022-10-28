package com.example.myapp.controller.interceptor;

import com.example.myapp.controller.ApiController;
import com.example.myapp.service.AccessTokenService;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/20
 */
public class AuthInterceptorTest {
    private final AccessTokenService accessTokenService = mock(AccessTokenService.class);
    private final AuthInterceptor authInterceptor = new AuthInterceptor(accessTokenService);
    private final int totalApiService = 4;

    @Before
    public void setUp() {
        authInterceptor.postProcessBeforeInitialization(new ApiController(null,
                        null, null, null),
                "apiController");
    }

    private int getAuthPathSize() {
        Set<String> authPaths = Whitebox.getInternalState(authInterceptor, "needAuthUri");
        return authPaths.size();
    }

    private boolean isNeedAuthRequest(String path) throws Exception {
        HttpServletRequest request = buildMockRequest(path);
        return Whitebox.invokeMethod(authInterceptor, "isNeedAuthRequest", request);
    }

    private HttpServletRequest buildMockRequest(String path) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }

    @Test
    public void postProcessBeforeInitializationAddPathsIfApiController() throws Exception {
        assertTrue(isNeedAuthRequest("/api/sendText"));
        assertTrue(isNeedAuthRequest("/api/sendMessageCard"));
        assertTrue(isNeedAuthRequest("/api/sendTopCard"));
    }

    @Test
    public void postProcessBeforeInitializationNotAddPathsIfNotController() {
        assertEquals(totalApiService, getAuthPathSize());
        authInterceptor.postProcessBeforeInitialization(new AuthInterceptor(null),
                "authInterceptor");
        assertEquals(totalApiService, getAuthPathSize());
    }

    @RestController
    private static class NoTokenRequiredController {
    }

    @Test
    public void postProcessBeforeInitializationNotAddPathsIfControllerButNotTokenRequired() {
        assertEquals(totalApiService, getAuthPathSize());
        authInterceptor.postProcessBeforeInitialization(new NoTokenRequiredController(),
                "noTokenRequiredController");
        assertEquals(totalApiService, getAuthPathSize());
    }

    @Test
    public void preHandleReturnTrueIfAuthRequestAndTokenNotExpired() throws Exception {
        HttpServletRequest request = buildMockRequest("/api/sendText");
        when(accessTokenService.isTokenNearlyExpired()).thenReturn(false);
        boolean ret = authInterceptor.preHandle(request, mock(HttpServletResponse.class), 1);
        verify(accessTokenService).isTokenNearlyExpired();
        assertTrue(ret);
    }

    @Test
    public void preHandleReturnTrueIfNotAuthRequest() throws Exception {
        HttpServletRequest request = buildMockRequest("/api/notExist");
        boolean ret = authInterceptor.preHandle(request, mock(HttpServletResponse.class), 1);
        assertTrue(ret);
    }

    @Test
    public void preHandleReturnFalseIfAuthRequestTokenExpired() throws Exception {
        HttpServletRequest request = buildMockRequest("/api/sendText");
        when(accessTokenService.isTokenNearlyExpired()).thenReturn(true);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));

        boolean ret = authInterceptor.preHandle(request, response, 1);
        assertFalse(ret);
    }
}