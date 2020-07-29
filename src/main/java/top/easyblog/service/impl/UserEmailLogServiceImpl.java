package top.easyblog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.entity.po.UserMailLog;
import top.easyblog.mapper.UserMailLogMapper;
import top.easyblog.service.IUserEmailLogService;

/**
 * @author huangxin
 */
@Service
public class UserEmailLogServiceImpl implements IUserEmailLogService {

    @Autowired
    private UserMailLogMapper userMailLogMapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public void saveSendCaptchaCode2User(String email, String content) {
        userMailLogMapper.save(new UserMailLog(email,content));
    }


}
