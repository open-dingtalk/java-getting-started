package com.example.myapp.controller;

import com.example.myapp.controller.model.MessageCardModel;
import com.example.myapp.controller.model.ServiceResponse;
import com.example.myapp.controller.model.TextModel;
import com.example.myapp.controller.model.TopCardModel;
import com.example.myapp.service.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/18
 */
public class ApiControllerTest {
    private final RobotGroupMessagesService robotGroupMessagesService = mock(RobotGroupMessagesService.class);
    private final RobotInteractiveCardsService robotInteractiveCardsService = mock(RobotInteractiveCardsService.class);
    private final InteractiveCardsInstanceService interactiveCardsInstanceService =
            mock(InteractiveCardsInstanceService.class);
    private final UserService userService = mock(UserService.class);
    private final ApiController apiController = new ApiController(robotGroupMessagesService,
            robotInteractiveCardsService, interactiveCardsInstanceService, userService);

    @Test
    public void sendTextReturnOKIfValidText() throws Exception {
        TextModel textModel = new TextModel();
        textModel.setOpenConversationId("openConversationId");
        textModel.setTxt("text");

        String response = apiController.sendText(textModel);
        assertEquals("OK", response);
        verify(robotGroupMessagesService).send("openConversationId", "text");
    }

    @Test
    public void sendMessageCardReturnOKIfValidMessageCard() throws Exception {
        MessageCardModel messageCardModel = new MessageCardModel();
        messageCardModel.setOpenConversationId("openConversationId");

        String response = apiController.sendMessageCard(messageCardModel);
        assertEquals("OK", response);
        verify(robotInteractiveCardsService).send(any());
    }

    @Test
    public void sendTopCardReturnOKIfValidTopCard() throws Exception {
        TopCardModel topCardModel = new TopCardModel();
        topCardModel.setOpenConversationId("openConversationId");

        String response = apiController.sendTopCard(topCardModel);
        verify(interactiveCardsInstanceService).createAndDeliverBox(any());
        assertEquals("OK", response);
    }

    @Test
    public void getUserInfoReturnUserInfoIfValidAuthCode() throws Exception {
        UserInfo expectUserInfo = new UserInfo();
        expectUserInfo.setUserId("userId");
        expectUserInfo.setName("name");
        expectUserInfo.setAvatar("avatar");

        when(userService.getUserInfo("authCode")).thenReturn(expectUserInfo);
        ServiceResponse<UserInfo> serviceResponse = apiController.getUserInfo("authCode");

        assertTrue(serviceResponse.isSuccess());
        assertEquals("ok", serviceResponse.getMessage());
        assertEquals(new Integer(200), serviceResponse.getCode());

        UserInfo userInfo = serviceResponse.getData();
        assertEquals(expectUserInfo.getUserId(), userInfo.getUserId());
        assertEquals(expectUserInfo.getName(), userInfo.getName());
        assertEquals(expectUserInfo.getAvatar(), userInfo.getAvatar());
    }
}