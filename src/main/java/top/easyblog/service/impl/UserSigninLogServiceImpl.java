package top.easyblog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.entity.po.UserSigninLog;
import top.easyblog.mapper.UserSigninLogMapper;
import top.easyblog.service.IUserSigninLogService;

import java.util.List;

/**
 * @author huangxin
 */
@Service
public class UserSigninLogServiceImpl implements IUserSigninLogService {

    @Autowired
    private UserSigninLogMapper userSigninLogMapper;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public int saveSigninLog(UserSigninLog log) {
        try {
            return userSigninLogMapper.save(log);
        } catch (Exception e) {
            e.getMessage();
            return -1;
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Cacheable(cacheNames = "user_login_info", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<UserSigninLog> getUserLoginInfo(int userId, int num) {
        if (userId > 0) {
            try {
                return userSigninLogMapper.getUserLoginInfo(userId, num);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
