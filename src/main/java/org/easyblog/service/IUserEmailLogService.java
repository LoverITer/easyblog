package org.easyblog.service;

public interface IUserEmailLogService {

   void saveSendCaptchaCode2User(String email, String content);

}
