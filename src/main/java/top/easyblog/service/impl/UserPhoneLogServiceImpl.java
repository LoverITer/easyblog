package top.easyblog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.entity.po.UserPhoneLog;
import top.easyblog.mapper.UserPhoneLogMapper;
import top.easyblog.service.IUserPhoneLogService;


/**
 * @author huangxin
 */
@Service
public class UserPhoneLogServiceImpl implements IUserPhoneLogService {

    final
    UserPhoneLogMapper userPhoneLogMapper;

    public UserPhoneLogServiceImpl(UserPhoneLogMapper userPhoneLogMapper) {
        this.userPhoneLogMapper = userPhoneLogMapper;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public void saveSendCaptchaCode2User(String phone, String content) {
        userPhoneLogMapper.save(new UserPhoneLog(phone,content));
    }
}
