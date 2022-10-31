[![Build Status](https://github.com/open-dingtalk/java-getting-started/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)](https://github.com/open-dingtalk/java-getting-started/actions)
![GitHub issues](https://img.shields.io/github/issues/open-dingtalk/java-getting-started)
![GitHub](https://img.shields.io/github/license/open-dingtalk/java-getting-started)

# 1 准备工作

## 1.1 DingTalk OpenAPI Java SDK下载

跳转到这个页面https://open.dingtalk.com/document/orgapp-server/sdk-download
参考文档下载即可。

## 1.2 JDK & mvn

jdk版本大于等于1.8即可

mvn版本大于等于3.2.5即可


# 2. 核心代码介绍

## 2.1 AuthInterceptor

    主要是用于请求前的统一token拦截检查，通过注解@TokenRequired来标注哪些请求是需要携带Token的。


## 2.2 AccessTokenService
    
    主要负责向钉钉开发平台的Token管理，启动强依赖，当Token接近过期的时候，去提前重新刷Token。

# 3. 如何启动

## 3.1 配置好源码里面application.properties

或者你可以拷贝一份放到外部目录

```properties
# 可以通过 https://open.dingtalk.com/document/orgapp-server/obtain-the-access_token-of-an-internal-app
# 来获取
app.appSecret=your appSecret
app.appKey=you appKey

# 可以通过 https://open.dingtalk.com/document/group/the-robot-sends-a-group-message
robot.code=your robot code

coolApp.code=you cool app code

card.messageCardTemplateId001=your message card templateId
card.topCardTemplateId001=your top card template
```

## 3.2 启动

首先在当前目录进行编译
```shell
mvn package -DskipTests
```

如果你是修改了源码里面的application.properties的话，那么可以直接启动即可

```shell
java -jar ./target/myapp-0.1.0-SNAPSHOT.jar
```

如果你是拷贝到外层的话，加入你放application.properties的路径是/Users/My/config/java-getting-started/
```shell
java -jar ./target/myapp-0.1.0-SNAPSHOT.jar --spring.config.location=/Users/My/config/java-getting-started/
```

启动后当你看到Started MyappApplication相关日志的时候就说明已经启动成功了
```text
Started MyappApplication in 3.465 seconds (JVM running for 4.077)
```
