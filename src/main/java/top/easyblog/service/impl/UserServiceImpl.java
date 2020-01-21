package top.easyblog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.bean.User;
import top.easyblog.commons.enums.UserFreeze;
import top.easyblog.commons.enums.UserLock;
import top.easyblog.commons.enums.UserPower;
import top.easyblog.commons.utils.EncryptUtil;
import top.easyblog.commons.utils.FileUploadUtils;
import top.easyblog.commons.utils.RegexUtil;
import top.easyblog.config.web.Result;
import top.easyblog.mapper.UserMapper;
import top.easyblog.service.IUserService;

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
            if (EncryptUtil.getInstance().SHA1(inputOldPWD, "user").equals(var0.getUserPassword())) {
                result.setSuccess(true);
            } else {
                result.setMessage("旧密码输入错误");
            }
        } else {
            result.setMessage("用户未登录");
        }
        return result;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Result isNewPasswordSameOldPassword(String inputOldPWD, String newPWD) {
        Result result = new Result();
        result.setSuccess(false);
        if (inputOldPWD.equals(newPWD)) {
            result.setSuccess(true);
            result.setMessage("新旧密码不能一样");
        }
        return result;
    }


    public Result isPasswordLegal(String password) {
        Result result = new Result();
        result.setSuccess(true);
        if (password.length() < 11 || password.length() > 20) {
            result.setSuccess(false);
            result.setMessage("密码长度必须介于11-20个字符");
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
        String headImageUrl = FileUploadUtils.defaultAvatar();
        try {
            User user = new User(nickname, password, null, null, null, null, null, null, null, 0, 100000, headImageUrl, null, ipInfo, null, UserLock.UNLOCK.getStatus(), UserFreeze.UNFREEZE.getStatus(), UserPower.USER.getLevel(), 0, 0);
            if (RegexUtil.isEmail(account)) {
                user.setUserMail(account);
            } else if (RegexUtil.isMobile(account)) {
                user.setUserPhone(account);
            }
            return userMapper.save(user) > 0;
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
        if (userId > 0) {
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
