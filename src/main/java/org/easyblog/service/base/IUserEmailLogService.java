package org.easyblog.service.base;

public interface IUserEmailLogService {

   void saveSendCaptchaCode2User(String email, String content);

}
