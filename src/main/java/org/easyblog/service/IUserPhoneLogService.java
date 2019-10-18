package org.easyblog.service;

public interface IUserPhoneLogService {

    void saveSendCaptchaCode2User(String phone, String content);

}
