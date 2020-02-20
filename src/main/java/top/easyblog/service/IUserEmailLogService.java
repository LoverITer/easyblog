package top.easyblog.service;

/**
 * @author huanxin
 */
public interface IUserEmailLogService {
    /**
     * 保存向用户发送的短信验证码
     *
     * @param email   邮箱
     * @param content 验证消息文本
     */
    void saveSendCaptchaCode2User(String email, String content);

}
