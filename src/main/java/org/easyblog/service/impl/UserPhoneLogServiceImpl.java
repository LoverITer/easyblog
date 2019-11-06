package org.easyblog.service.impl;

import org.easyblog.bean.UserPhoneLog;
import org.easyblog.mapper.UserPhoneLogMapper;
import org.easyblog.service.IUserPhoneLogService;
import org.springframework.stereotype.Service;

@Service
public class UserPhoneLogServiceImpl implements IUserPhoneLogService {

    final
    UserPhoneLogMapper userPhoneLogMapper;

    public UserPhoneLogServiceImpl(UserPhoneLogMapper userPhoneLogMapper) {
        this.userPhoneLogMapper = userPhoneLogMapper;
    }

    @Override
    public void saveSendCaptchaCode2User(String phone, String content) {
        userPhoneLogMapper.save(new UserPhoneLog(phone,content));
    }
}
