package com.iot.elderly.elderlycarebackend.dto;

/**
 * 【废案】微信登录请求 DTO
 *
 * 这个类是为微信小程序登录准备的，项目后来放弃了微信小程序，所以已经不再使用。
 *
 * 原本用途：前端（微信小程序）调用 wx.login() 获取临时 code 后，
 *          将 code + userType 发送到 POST /api/auth/wechat/login 进行登录
 *
 * 字段说明：
 * - code: 微信登录临时凭证（wx.login() 返回的），有效期 5 分钟
 * - userType: 用户类型字符串（"elderly"/"family"/"admin"），在 Service 层转为 UserType 枚举
 */
public class WechatLoginRequest {
    private String code;
    private String userType; // 用String接收，后续在Service中转为枚举

    // Getter 和 Setter
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
