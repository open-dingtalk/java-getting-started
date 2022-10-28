package com.example.myapp.service;

import com.aliyun.dingtalkim_1_0.Client;
import com.aliyun.dingtalkim_1_0.models.*;
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
 * 吊顶卡片接口，详见https://open.dingtalk.com/document/orgapp-server/create-an-interactive-card-instance-1
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/19
 */
@Slf4j
@Service
public class InteractiveCardsInstanceService {
    private Client client;
    private final AccessTokenService accessTokenService;

    @Value("${card.topCardTemplateId001}")
    private String topCardTemplateId;

    @Value("${robot.code}")
    private String robotCode;

    private static final Integer CONVERSATION_GROUP_TYPE = 1;

    @Autowired
    public InteractiveCardsInstanceService(AccessTokenService accessTokenService) {
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
     * @throws Exception may http exception or service exception
     */
    public Boolean createAndDeliverBox(String openConversationId) throws Exception {
        try {
            String outTrackId = instance(openConversationId);
            if (Objects.isNull(outTrackId)) {
                return false;
            }

            log.info("InteractiveCardsInstanceService_createAndDeliverBox instance success," +
                    " openConversationId={}, outTrackId={}.", openConversationId, outTrackId);
            return openTopBoxes(openConversationId, outTrackId);
        } catch (TeaException e) {
            log.error("InteractiveCardsInstanceService_createAndDeliverBox throw TeaException, errCode={}, " +
                    "errorMessage={}", e.getCode(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("InteractiveCardsInstanceService_createAndDeliverBox throw Exception", e);
            throw e;
        }
    }

    private String instance(String openConversationId) throws Exception {
        InteractiveCardCreateInstanceHeaders headers = new InteractiveCardCreateInstanceHeaders();
        headers.xAcsDingtalkAccessToken = accessTokenService.getAccessToken();

        InteractiveCardCreateInstanceRequest request = new InteractiveCardCreateInstanceRequest();
        request.setCardTemplateId(topCardTemplateId);
        request.setOpenConversationId(openConversationId);
        request.setOutTrackId("topCardId"+ UUID.randomUUID());
        request.setRobotCode(robotCode);
        request.setConversationType(CONVERSATION_GROUP_TYPE);

        InteractiveCardCreateInstanceRequest.InteractiveCardCreateInstanceRequestCardData cardData =
                new InteractiveCardCreateInstanceRequest.InteractiveCardCreateInstanceRequestCardData();
        cardData.setCardParamMap(new HashMap<>(1));

        request.setCardData(cardData);

        InteractiveCardCreateInstanceResponse response = client.interactiveCardCreateInstanceWithOptions(request,
                headers, new RuntimeOptions());
        if (Objects.isNull(response) || Objects.isNull(response.getBody()) ||
                Objects.isNull(response.body.processQueryKey) ||
                response.body.processQueryKey.isEmpty()) {
            log.error("InteractiveCardsInstanceService_instance interactiveCardCreateInstanceWithOptions return" +
                    " error, response={}", response);
            return null;
        }

        return request.getOutTrackId();
    }

    private boolean openTopBoxes(String openConversationId, String outTrackId) throws Exception {
        TopboxOpenHeaders topboxOpenHeaders = new TopboxOpenHeaders();
        topboxOpenHeaders.xAcsDingtalkAccessToken = accessTokenService.getAccessToken();

        TopboxOpenRequest topboxOpenRequest = new TopboxOpenRequest();
        topboxOpenRequest.setPlatforms("ios|mac|android|win");
        topboxOpenRequest.setOpenConversationId(openConversationId);
        topboxOpenRequest.setOutTrackId(outTrackId);
        topboxOpenRequest.setRobotCode(robotCode);
        topboxOpenRequest.setExpiredTime(System.currentTimeMillis() + 5*60*1000);

        TopboxOpenResponse topboxOpenResponse = client.topboxOpenWithOptions(topboxOpenRequest, topboxOpenHeaders,
                new RuntimeOptions());
        return !Objects.isNull(topboxOpenResponse);
    }
}
