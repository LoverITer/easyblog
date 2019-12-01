package top.easyblog.service.impl;

import top.easyblog.bean.User;
import top.easyblog.config.web.Result;
import top.easyblog.enumHelper.UserFreeze;
import top.easyblog.enumHelper.UserLock;
import top.easyblog.enumHelper.UserPower;
import top.easyblog.handler.exception.NullUserException;
import top.easyblog.mapper.UserMapper;
import top.easyblog.service.IUserService;
import top.easyblog.utils.EncryptUtil;
import top.easyblog.utils.FileUploadUtils;
import top.easyblog.utils.RegexUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserServiceImpl implements IUserService {


    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
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
    public Result isAuthorized(User user, String inputOldPWD) {
        User var0 = getUser(user.getUserId());
        Result result = new Result();
        result.setSuccess(false);
        if (var0 != null) {
            if (EncryptUtil.getInstance().DESEncode(inputOldPWD, "user").equals(var0.getUserPassword())) {
                result.setSuccess(true);
            }else{
                result.setMsg("旧密码输入错误");
            }
        }else{
            result.setMsg("用户未登录");
        }
        return result;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Result isNewPasswordSameOldPassword(String inputOldPWD, String newPWD) {
        Result result = new Result();
        result.setSuccess(false);
        if (inputOldPWD.equals(newPWD)) {
            result.setSuccess(true);
            result.setMsg("新旧密码不能一样");
        }
        return result;
    }


    public Result isPasswordLegal(String password) {
        Result result = new Result();
        result.setSuccess(true);
        if (password.length() < 11 || password.length() > 20) {
            result.setSuccess(false);
            result.setMsg("密码长度必须介于11-20个字符");
        }
        return result;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
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
    @Override
    public User getUser(long uid) {
        return userMapper.getByPrimaryKey(uid);
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public boolean register(String nickname, String password, String account, String ipInfo) {
        String headUrl = FileUploadUtils.defaultAvatar();
        try {
            User user = null;
            if (RegexUtil.isEmail(account)) {
                user = new User(nickname, password, null, null, null, null, null, account, null, 0, 100000, headUrl, null, ipInfo, null, UserLock.UNLOCK.getStatus(), UserFreeze.UNFREEZE.getStatus(), UserPower.USER.getLevel(), 0, 0);
            } else if (RegexUtil.isMobile(account)) {
                user = new User(nickname, password, null, null, null, null, null, null, account, 0, 100000, headUrl, null, ipInfo, null, UserLock.UNLOCK.getStatus(), UserFreeze.UNFREEZE.getStatus(), UserPower.USER.getLevel(), 0, 0);
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public int updateUserInfo(String account, String newPassword) {
        try {
            User user = new User();
            if (RegexUtil.isEmail(account)) {
                user.setUserMail(account);
            } else if (RegexUtil.isPhone(account)) {
                user.setUserPhone(account);
            }
            user.setUserPassword(newPassword);
            return userMapper.updateUserSelective(user);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public int updateUserInfo(User user) {
        if (Objects.nonNull(user)) {
            try {
                return userMapper.updateByPrimaryKeySelective(user);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Transactional
    @Override
    public int deleteUserByPK(int userId) {
        if(userId>0) {
            try {
                userMapper.deleteByPrimaryKey((long) userId);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }
}
