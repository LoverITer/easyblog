package top.easyblog.service.impl;

import top.easyblog.bean.UserMailLog;
import top.easyblog.mapper.UserMailLogMapper;
import top.easyblog.service.IUserEmailLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserEmailLogServiceImpl implements IUserEmailLogService {

    @Autowired
    private UserMailLogMapper userMailLogMapper;

    @Override
    public void saveSendCaptchaCode2User(String email, String content) {
        userMailLogMapper.save(new UserMailLog(email,content));
    }


}
