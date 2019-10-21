package org.easyblog.service;

import org.easyblog.bean.UserSigninLog;
import org.easyblog.mapper.UserSigninLogMapper;
import org.easyblog.service.base.IUserSigninLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSigninLogServiceImpl implements IUserSigninLogService {

    @Autowired
    private UserSigninLogMapper userSigninLogMapper;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    @Override
    public int saveSigninLog(UserSigninLog log) {
        try {
            return userSigninLogMapper.save(log);
        }catch (Exception e){
            e.getMessage();
            return -1;
        }
    }
}
