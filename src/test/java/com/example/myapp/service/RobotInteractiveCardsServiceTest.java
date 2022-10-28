package com.example.myapp.service;

import com.aliyun.dingtalkim_1_0.Client;
import com.aliyun.dingtalkim_1_0.models.SendRobotInteractiveCardResponse;
import com.aliyun.dingtalkim_1_0.models.SendRobotInteractiveCardResponseBody;
import com.aliyun.tea.TeaException;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/19
 */
public class RobotInteractiveCardsServiceTest {
    private final AccessTokenService accessTokenService = mock(AccessTokenService.class);
    private final RobotInteractiveCardsService robotInteractiveCardsService = new RobotInteractiveCardsService(accessTokenService);
    private final Client client = mock(Client.class);
    private final String openConversationId = "openConversationId";

    @Before
    public void setUp() {
        Whitebox.setInternalState(robotInteractiveCardsService, "client", client);
    }

    @Test
    public void initNotThrowExceptionIfCommon() throws Exception {
        robotInteractiveCardsService.init();
    }

    @Test
    public void sendReturnProcessQueryKeyIfClientSendReturnQueryMessageId() throws Exception {
        SendRobotInteractiveCardResponse sendRobotInteractiveCardResponse = new SendRobotInteractiveCardResponse();
        SendRobotInteractiveCardResponseBody body = new SendRobotInteractiveCardResponseBody();
        body.setProcessQueryKey("processQueryKey");
        sendRobotInteractiveCardResponse.setBody(body);
        when(client.sendRobotInteractiveCardWithOptions(any(), any(), any())).
                thenReturn(sendRobotInteractiveCardResponse);
        String processQueryKey = robotInteractiveCardsService.send(openConversationId);
        assertEquals("processQueryKey", processQueryKey);
    }

    @Test(expected = TeaException.class)
    public void sendThrowTeaExceptionIfClientSendTeaException() throws Exception {
        when(client.sendRobotInteractiveCardWithOptions(any(), any(), any())).thenThrow(new TeaException());
        robotInteractiveCardsService.send(openConversationId);
    }

    @Test(expected = Exception.class)
    public void sendThrowExceptionIfClientSendException() throws Exception {
        when(client.sendRobotInteractiveCardWithOptions(any(), any(), any())).thenThrow(new RuntimeException());
        robotInteractiveCardsService.send(openConversationId);
    }

    @Test
    public void sendReturnNullIfClientSendReturnNullResponse() throws Exception {
        when(client.sendRobotInteractiveCardWithOptions(any(), any(), any())).thenReturn(null);
        String messageId = robotInteractiveCardsService.send(openConversationId);
        assertNull(messageId);
    }

    @Test
    public void sendReturnNullIfClientSendReturnResponseBodyIsNull() throws Exception {
        SendRobotInteractiveCardResponse sendRobotInteractiveCardResponse = new SendRobotInteractiveCardResponse();
        when(client.sendRobotInteractiveCardWithOptions(any(), any(), any())).thenReturn(sendRobotInteractiveCardResponse);
        String messageId = robotInteractiveCardsService.send(openConversationId);
        assertNull(messageId);
    }
}