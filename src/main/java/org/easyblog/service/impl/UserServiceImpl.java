package org.easyblog.service.impl;

import org.easyblog.bean.User;
import org.easyblog.bean.enums.UserFreeze;
import org.easyblog.bean.enums.UserLock;
import org.easyblog.bean.enums.UserPower;
import org.easyblog.handler.exception.NullUserException;
import org.easyblog.mapper.UserMapper;
import org.easyblog.service.IUserService;
import org.easyblog.utils.FileUploadUtils;
import org.easyblog.utils.RegexUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@CacheConfig(keyGenerator = "keyGenerator", cacheManager = "cacheManager")
@Service
public class UserServiceImpl implements IUserService {


    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "user",unless = "#result==null")
    @Override
    public User checkUser(String username, String password) {
        User user = null;
        if (RegexUtil.isEmail(username)) {
            user = userMapper.getUserByUserEmailAndPassword(username, password);
        } else if (RegexUtil.isPhone(username)) {
            user = userMapper.getUserByUserPhoneAndPassword(username, password);
        } else if (RegexUtil.isQQ(username)) {
            user = userMapper.getUserByUserQQAndPassword(username, password);
        }

        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "user",condition = "#result!=null")
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "user", condition = "#result!=null")
    @Override
    public User getUser(long uid) {
        return userMapper.getByPrimaryKey(uid);
    }



    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @CachePut(cacheNames = "user",condition = "#result==true")
    @Override
    public boolean register(String nickname, String password, String account, String ipInfo) {
        String headUrl= FileUploadUtils.defaultAvatar();
        try {
            User user = null;
            if (RegexUtil.isEmail(account)) {
                user = new User(nickname, password, null, null, null, null, null, account, null, 0, 100000, headUrl, null, ipInfo, null, UserLock.UNLOCK.getStatus(), UserFreeze.UNFREEZE.getStatus(), UserPower.USER.getLevel(),0,0);
            } else if (RegexUtil.isMobile(account)) {
                user = new User(nickname, password, null, null, null, null, null, null, account, 0, 100000, headUrl, null, ipInfo, null, UserLock.UNLOCK.getStatus(), UserFreeze.UNFREEZE.getStatus(), UserPower.USER.getLevel(),0,0);
            }
            if (user != null) {
                return userMapper.save(user) > 0;
            } else {
                throw new NullUserException("用户不可为空！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @CachePut(cacheNames = "user",condition = "#result==true")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public boolean updateUserInfo(String account,String newPassword) {
        try {
            User user = new User();
            if(RegexUtil.isEmail(account)){
                user.setUserMail(account);
            }else if(RegexUtil.isPhone(account)){
                user.setUserPhone(account);
            }
            user.setUserPassword(newPassword);
            userMapper.updateUserSelective(user);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
