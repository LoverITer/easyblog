package org.easyblog.service;

import org.easyblog.bean.User;
import org.easyblog.bean.enums.UserFreeze;
import org.easyblog.bean.enums.UserLock;
import org.easyblog.bean.enums.UserPower;
import org.easyblog.handler.exception.NullUserException;
import org.easyblog.mapper.UserMapper;
import org.easyblog.utils.RegexUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements IUserService {


    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public User checkUser(String username, String password) {
        User user = userMapper.getUserByUserEmailAndPassword(username, password);
        if (Objects.isNull(user)) {
            user = userMapper.getUserByUserPhoneAndPassword(username, password);
        }
        if (Objects.isNull(user)) {
            user = userMapper.getUserByUserQQAndPassword(username, password);
        }
        return user;
    }

    @Override
    public User getUser(String queryStr) {
        User user = null;
        if (RegexUtil.isMobile(queryStr)) {
            user = userMapper.getUserByPhone(queryStr);
        } else if (RegexUtil.isEmail(queryStr)) {
            user = userMapper.getUserByEmail(queryStr);
        } else {
            user = userMapper.getUserByNickname(queryStr);
        }
        return user;
    }


    @Override
    public boolean register(String nickname, String password, String account, String ipInfo) {

        try {
            User user = null;
            if (RegexUtil.isEmail(account)) {
                user = new User(nickname, password, null, null, null, null, null, account, null, 0, 100000, null, null, ipInfo, null, null, UserLock.UNLOCK.getStatus(), UserFreeze.UNFREEZE.getStatus(), UserPower.USER.getLevel());
            } else if (RegexUtil.isMobile(account)) {
                user = new User(nickname, password, null, null, null, null, null, null, account, 0, 100000, null, null, ipInfo, null, null, UserLock.UNLOCK.getStatus(), UserFreeze.UNFREEZE.getStatus(), UserPower.USER.getLevel());
            }
            if(user!=null) {
                userMapper.save(user);
                return true;
            }else{
                throw new NullUserException("用户不可为空！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
