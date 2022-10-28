package com.example.myapp.service;

import com.aliyun.dingtalkrobot_1_0.Client;
import com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendResponse;
import com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendResponseBody;
import com.aliyun.tea.TeaException;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/19
 */
public class RobotGroupMessagesServiceTest {
    private final AccessTokenService accessTokenService = mock(AccessTokenService.class);
    private final RobotGroupMessagesService robotGroupMessagesService = new RobotGroupMessagesService(accessTokenService);
    private final Client client = mock(Client.class);
    private final String openConversationId = "open";

    @Before
    public void setUp() {
        Whitebox.setInternalState(robotGroupMessagesService, "robotClient", client);
    }

    @Test
    public void initNotThrowExceptionIfCommon() throws Exception {
        robotGroupMessagesService.init();
    }

    @Test
    public void sendReturnMessageIdIfClientSendReturnQueryMessageId() throws Exception {
        OrgGroupSendResponse orgGroupSendResponse = new OrgGroupSendResponse();
        OrgGroupSendResponseBody orgGroupSendResponseBody = new OrgGroupSendResponseBody();
        orgGroupSendResponseBody.setProcessQueryKey("messageId");
        orgGroupSendResponse.setBody(orgGroupSendResponseBody);
        when(client.orgGroupSendWithOptions(any(), any(), any())).thenReturn(orgGroupSendResponse);
        String messageId = robotGroupMessagesService.send(openConversationId, "text");
        assertEquals("messageId", messageId);
    }

    @Test(expected = TeaException.class)
    public void sendThrowTeaExceptionIfClientSendTeaException() throws Exception {
        when(client.orgGroupSendWithOptions(any(), any(), any())).thenThrow(new TeaException());
        robotGroupMessagesService.send(openConversationId, "text");
    }

    @Test(expected = Exception.class)
    public void sendThrowExceptionIfClientSendException() throws Exception {
        when(client.orgGroupSendWithOptions(any(), any(), any())).thenThrow(new RuntimeException());
        robotGroupMessagesService.send(openConversationId, "text");
    }

    @Test
    public void sendReturnNullIfClientSendReturnNullResponse() throws Exception {
        when(client.orgGroupSendWithOptions(any(), any(), any())).thenReturn(null);
        String messageId = robotGroupMessagesService.send(openConversationId, "text");
        assertNull(messageId);
    }

    @Test
    public void sendReturnNullIfClientSendReturnResponseBodyIsNull() throws Exception {
        OrgGroupSendResponse orgGroupSendResponse = new OrgGroupSendResponse();
        when(client.orgGroupSendWithOptions(any(), any(), any())).thenReturn(orgGroupSendResponse);
        String messageId = robotGroupMessagesService.send(openConversationId, "text");
        assertNull(messageId);
    }
}