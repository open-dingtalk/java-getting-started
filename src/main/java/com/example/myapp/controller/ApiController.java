package com.example.myapp.controller;

import com.example.myapp.controller.interceptor.TokenRequired;
import com.example.myapp.controller.model.MessageCardModel;
import com.example.myapp.controller.model.ServiceResponse;
import com.example.myapp.controller.model.TextModel;
import com.example.myapp.controller.model.TopCardModel;
import com.example.myapp.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/18
 */
@Slf4j
@TokenRequired
@RestController
@RequestMapping("/api")
public class ApiController {
    private final RobotGroupMessagesService robotGroupMessagesService;
    private final RobotInteractiveCardsService robotInteractiveCardsService;
    private final InteractiveCardsInstanceService interactiveCardsInstanceService;
    private final UserService userService;

    @Autowired
    public ApiController(RobotGroupMessagesService robotGroupMessagesService,
                         RobotInteractiveCardsService robotInteractiveCardsService,
                         InteractiveCardsInstanceService interactiveCardsInstanceService, UserService userService) {
        this.robotGroupMessagesService = robotGroupMessagesService;
        this.robotInteractiveCardsService = robotInteractiveCardsService;
        this.interactiveCardsInstanceService = interactiveCardsInstanceService;
        this.userService = userService;
    }

    /**
     * 发送文本消息
     * @return Response 200 : OK
     */
    @PostMapping(value = "/sendText")
    public String sendText(@RequestBody TextModel textModel) throws Exception {
        String messageId = robotGroupMessagesService.send(textModel.getOpenConversationId(),
                textModel.getTxt());
        log.info("sendText success {}, messageId={}", textModel, messageId);
        return "OK";
    }

    /**
     * 发送消息卡片
     * @param messageCardModel 消息卡片结构体
     * @return OK
     * @throws Exception Tea异常 & 内部异常
     */
    @PostMapping(value = "/sendMessageCard")
    public String sendMessageCard(@RequestBody MessageCardModel messageCardModel) throws Exception {
        robotInteractiveCardsService.send(messageCardModel.getOpenConversationId());
        log.info("sendMessageCard success {}", messageCardModel);
        return "OK";
    }

    /**
     * 发送吊顶卡片
     * @param topCardModel 吊顶卡片model
     * @return OK
     * @throws Exception 内部的一些异常
     */
    @PostMapping(value = "/sendTopCard")
    public String sendTopCard(@RequestBody TopCardModel topCardModel) throws Exception {
        log.info("sendTopCard {}", topCardModel);
        Boolean success = interactiveCardsInstanceService.createAndDeliverBox(topCardModel.getOpenConversationId());
        log.info("sendTopCard success {}, model={}", success, topCardModel);
        return "OK";
    }

    /**
     * 获取用户信息
     * @param authCode 免等授权码
     * @return ServiceResponse isSuccess说明是正常，否则异常，异常可以通过True Or False来体现
     * @throws Exception 内部异常
     */
    @GetMapping(value = "/getUserInfo")
    public ServiceResponse<UserInfo> getUserInfo(@RequestParam("requestAuthCode") String authCode) throws Exception {
        UserInfo userInfo = userService.getUserInfo(authCode);
        log.info("getUserInfo, md5AuthCode={}, userInfo={}", DigestUtils.md5DigestAsHex(
                authCode.getBytes()), userInfo);
        return ServiceResponse.buildSuccessServiceResponse(userInfo);
    }
}
