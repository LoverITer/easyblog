package org.easyblog.service.base;

public interface IUserPhoneLogService {

    void saveSendCaptchaCode2User(String phone, String content);

}
