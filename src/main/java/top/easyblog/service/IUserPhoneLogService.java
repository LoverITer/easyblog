package top.easyblog.service;

/**
 * @author huanxin
 */
public interface IUserPhoneLogService {

    /**
     * 保存向用户发送的短信验证码
     *
     * @param phone   手机
     * @param content 验证消息文本
     */
    void saveSendCaptchaCode2User(String phone, String content);

}
