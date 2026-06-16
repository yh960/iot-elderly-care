package com.iot.elderly.elderlycarebackend.service;

import com.iot.elderly.elderlycarebackend.entity.AlertType;
import org.springframework.stereotype.Service;

/**
 * 【废案】微信消息推送服务
 *
 * 这个服务是为微信小程序订阅消息准备的，项目后来放弃了微信小程序，所以当前只有 TODO 桩实现（打印日志）。
 *
 * 原本的设计流程：
 * 1. 老人在小程序中授权订阅"跌倒报警"模板消息
 * 2. 当系统检测到跌倒时，调用这个服务向家属发送微信订阅消息
 * 3. 家属在微信中收到通知，点击可查看详情
 *
 * 接入步骤（TODO，未实现）：
 * 1. 在 application.yml 配置 wechat.appid、wechat.secret、wechat.template_id
 * 2. 调用微信 API 获取 access_token
 * 3. 调用订阅消息发送接口
 *
 * 当前状态：只打印日志到控制台，不影响程序运行
 */
@Service
public class WxPushService {
    /**
     * 向家属发送微信小程序订阅消息
     */
    public void sendAlertNotification(Long familyUserId, AlertType alertType) {
        // TODO: 真实接入步骤：
        //   1. application.yml 配置 wechat.appid、wechat.secret、wechat.template_id
        //   2. GET https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={}&secret={} 获取 access_token
        //   3. POST https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={} 发送订阅消息
        //   4. 注意：用户需先在小程序端授权订阅该模板消息，否则发送会失败
        System.out.println("========== 【微信消息推送】 ==========");
        System.out.println("发送给家属ID: " + familyUserId);
        System.out.println("报警类型: " + alertType.getDescription());
        System.out.println("消息内容: 您家中老人可能发生" + alertType.getDescription() + "，请立即关注！");
        System.out.println("======================================");
    }
}
