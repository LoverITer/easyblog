package top.easyblog.service;

public interface IUserPhoneLogService {

    void saveSendCaptchaCode2User(String phone, String content);

}
