package com.example.myapp.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkrobot_1_0.Client;
import com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendHeaders;
import com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendRequest;
import com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendResponse;
import com.aliyun.tea.TeaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * 钉钉机器人消息服务
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/19
 */
@Slf4j
@Service
public class RobotGroupMessagesService {
    private Client robotClient;
    private final AccessTokenService accessTokenService;

    @Value("${robot.code}")
    private String robotCode;

    @Value("${coolApp.code}")
    private String coolAppCode;

    @Autowired
    public RobotGroupMessagesService(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @PostConstruct
    public void init() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        robotClient =  new com.aliyun.dingtalkrobot_1_0.Client(config);
    }

    /**
     * 内部群发消息
     * @throws Exception e
     * @param openConversationId 群id
     * @return 加密消息id
     */
    public String send(String openConversationId, String text) throws Exception {
        OrgGroupSendHeaders orgGroupSendHeaders = new OrgGroupSendHeaders();
        orgGroupSendHeaders.setXAcsDingtalkAccessToken(accessTokenService.getAccessToken());

        OrgGroupSendRequest orgGroupSendRequest = new OrgGroupSendRequest();
        orgGroupSendRequest.setMsgKey("sampleText");
        orgGroupSendRequest.setRobotCode(robotCode);
        orgGroupSendRequest.setCoolAppCode(coolAppCode);
        orgGroupSendRequest.setOpenConversationId(openConversationId);

        JSONObject msgParam = new JSONObject();
        msgParam.put("content", "java-getting-stated say : " + text);
        orgGroupSendRequest.setMsgParam(msgParam.toJSONString());

        try {
            OrgGroupSendResponse orgGroupSendResponse = robotClient.orgGroupSendWithOptions(orgGroupSendRequest,
                    orgGroupSendHeaders, new com.aliyun.teautil.models.RuntimeOptions());
            if (Objects.isNull(orgGroupSendResponse) || Objects.isNull(orgGroupSendResponse.getBody())) {
                log.error("RobotGroupMessagesService_send orgGroupSendWithOptions return error, response={}",
                        orgGroupSendResponse);
                return null;
            }

            return orgGroupSendResponse.getBody().getProcessQueryKey();
        } catch (TeaException e) {
            log.error("RobotGroupMessagesService_send orgGroupSendWithOptions throw TeaException, errCode={}, " +
                    "errorMessage={}", e.getCode(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("RobotGroupMessagesService_send orgGroupSendWithOptions throw Exception", e);
            throw e;
        }
    }
}
