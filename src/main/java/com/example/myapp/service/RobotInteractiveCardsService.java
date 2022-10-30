package com.example.myapp.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkim_1_0.Client;
import com.aliyun.dingtalkim_1_0.models.SendRobotInteractiveCardHeaders;
import com.aliyun.dingtalkim_1_0.models.SendRobotInteractiveCardRequest;
import com.aliyun.dingtalkim_1_0.models.SendRobotInteractiveCardResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * 互动卡片服务
 *
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/19
 */
@Slf4j
@Service
public class RobotInteractiveCardsService {
    private Client client;
    private final AccessTokenService accessTokenService;

    @Value("${card.messageCardTemplateId001}")
    private String cardMessageTemplateId;

    @Value("${robot.code}")
    private String robotCode;

    @Autowired
    public RobotInteractiveCardsService(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @PostConstruct
    public void init() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        client = new Client(config);
    }

    /**
     * 机器人发送互动卡片
     *
     * @param openConversationId 详见https://open.dingtalk.com/document/group/robots-send-interactive-cards
     * @return 用于业务方后续查看已读列表的查询key。
     */
    public String send(String openConversationId) throws Exception {
        SendRobotInteractiveCardHeaders headers = new SendRobotInteractiveCardHeaders();
        headers.xAcsDingtalkAccessToken = accessTokenService.getAccessToken();

        SendRobotInteractiveCardRequest request = new SendRobotInteractiveCardRequest();
        request.setCardTemplateId(cardMessageTemplateId);
        request.setOpenConversationId(openConversationId);
        request.setCardBizId("msgcardid" + UUID.randomUUID());
        request.setRobotCode(robotCode);

        JSONObject cardData = new JSONObject();
        cardData.put("videoUrl", "https://cloud.video.taobao.com/play/u/null/p/1/e/6/t/1/d/ud/352793594610.mp4");
        request.setCardData(cardData.toJSONString(cardData));

        try {
            SendRobotInteractiveCardResponse response = client.sendRobotInteractiveCardWithOptions(request, headers,
                    new RuntimeOptions());
            if (Objects.isNull(response) || Objects.isNull(response.getBody())) {
                log.error("RobotInteractiveCardsService_send sendRobotInteractiveCardWithOptions return null, " +
                        "response={}", response);
                return null;
            }

            return response.getBody().processQueryKey;
        } catch (TeaException e) {
            log.error("RobotInteractiveCardsService_send sendRobotInteractiveCardWithOptions throw TeaException," +
                    " errCode={}, errorMessage={}", e.getCode(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("RobotInteractiveCardsService_send sendRobotInteractiveCardWithOptions throw Exception", e);
            throw e;
        }
    }
}
