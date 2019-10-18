package org.easyblog.service;

import org.easyblog.bean.UserPhoneLog;
import org.easyblog.mapper.UserPhoneLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPhoneLogServiceImpl implements IUserPhoneLogService {

    @Autowired
    UserPhoneLogMapper userPhoneLogMapper;

    @Override
    public void saveSendCaptchaCode2User(String phone, String content) {
        userPhoneLogMapper.save(new UserPhoneLog(phone,content));
    }
}
