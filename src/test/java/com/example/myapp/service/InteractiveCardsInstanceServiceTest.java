package com.example.myapp.service;

import com.aliyun.dingtalkim_1_0.Client;
import com.aliyun.dingtalkim_1_0.models.InteractiveCardCreateInstanceResponse;
import com.aliyun.dingtalkim_1_0.models.InteractiveCardCreateInstanceResponseBody;
import com.aliyun.dingtalkim_1_0.models.TopboxOpenResponse;
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
public class InteractiveCardsInstanceServiceTest {
    private final AccessTokenService accessTokenService = mock(AccessTokenService.class);
    private final InteractiveCardsInstanceService interactiveCardsInstanceService =
            new InteractiveCardsInstanceService(accessTokenService);
    private final Client client = mock(Client.class);
    private final String openConversationId = "openConversationId";
    private InteractiveCardCreateInstanceResponse interactiveCardCreateInstanceResponse;

    @Before
    public void setUp() {
        Whitebox.setInternalState(interactiveCardsInstanceService, "client", client);
        InteractiveCardCreateInstanceResponseBody interactiveCardCreateInstanceResponseBody =
                new InteractiveCardCreateInstanceResponseBody();
        interactiveCardCreateInstanceResponseBody.setProcessQueryKey("processKey");

        interactiveCardCreateInstanceResponse =
                new InteractiveCardCreateInstanceResponse();
        interactiveCardCreateInstanceResponse.setBody(interactiveCardCreateInstanceResponseBody);
    }

    @Test
    public void initNotThrowExceptionIfCommon() throws Exception {
        interactiveCardsInstanceService.init();
    }

    @Test
    public void createAndDeliverBoxReturnMessageIdIfInstanceAndOpenSuccess() throws Exception {
        // Mocking instance Success
        when(client.interactiveCardCreateInstanceWithOptions(any(), any(), any())).thenReturn(
                interactiveCardCreateInstanceResponse);

        // Mocking topBoxOpenWithOptions
        TopboxOpenResponse openResponse = new TopboxOpenResponse();
        when(client.topboxOpenWithOptions(any(), any(), any())).thenReturn(
                openResponse);

        Boolean success = interactiveCardsInstanceService.createAndDeliverBox(openConversationId);
        assertTrue(success);
    }

    @Test(expected = TeaException.class)
    public void createAndDeliverBoxThrowTeaExceptionIfInstanceThrowTeaException() throws Exception {
        when(client.interactiveCardCreateInstanceWithOptions(any(), any(), any())).thenThrow(new TeaException());
        interactiveCardsInstanceService.createAndDeliverBox(openConversationId);
    }

    @Test(expected = TeaException.class)
    public void createAndDeliverBoxThrowTeaExceptionIfTopBoxOpenThrowTeaException() throws Exception {
        when(client.interactiveCardCreateInstanceWithOptions(any(), any(), any())).thenReturn(
                interactiveCardCreateInstanceResponse);
        when(client.topboxOpenWithOptions(any(), any(), any())).thenThrow(
                new TeaException());
        interactiveCardsInstanceService.createAndDeliverBox(openConversationId);
    }

    @Test(expected = Exception.class)
    public void createAndDeliverBoxThrowExceptionIfInstanceSendException() throws Exception {
        when(client.interactiveCardCreateInstanceWithOptions(any(), any(), any())).thenThrow(new RuntimeException());
        interactiveCardsInstanceService.createAndDeliverBox(openConversationId);
    }

    @Test
    public void createAndDeliverBoxReturnFalseIfClientSendReturnNullResponse() throws Exception {
        when(client.interactiveCardCreateInstanceWithOptions(any(), any(), any())).thenReturn(null);
        Boolean success = interactiveCardsInstanceService.createAndDeliverBox(openConversationId);
        assertFalse(success);
    }

    @Test
    public void createAndDeliverBoxReturnFalseIfClientSendReturnResponseBodyIsNull() throws Exception {
        interactiveCardCreateInstanceResponse.setBody(null);
        when(client.interactiveCardCreateInstanceWithOptions(any(), any(), any())).thenReturn(
                interactiveCardCreateInstanceResponse);
        Boolean success = interactiveCardsInstanceService.createAndDeliverBox(openConversationId);
        assertFalse(success);
    }
}