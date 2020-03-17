package top.easyblog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.bean.User;
import top.easyblog.common.enums.UserFreeze;
import top.easyblog.common.enums.UserLock;
import top.easyblog.common.enums.UserPower;
import top.easyblog.common.util.DefaultImageDispatcherUtils;
import top.easyblog.common.util.EncryptUtils;
import top.easyblog.common.util.RegexUtils;
import top.easyblog.config.web.Result;
import top.easyblog.mapper.UserMapper;
import top.easyblog.service.IUserService;

import java.util.Objects;

/**
 * @author huangxin
 */
@Service
public class UserServiceImpl implements IUserService {


    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public User checkUser(String username, String password) {
        User user = null;
        if (RegexUtils.isEmail(username)) {
            user = userMapper.getUserByUserEmailAndPassword(username, password);
        } else if (RegexUtils.isPhone(username)) {
            user = userMapper.getUserByUserPhoneAndPassword(username, password);
        }

        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public Result isAuthorized(User user, String inputOldPWD) {
        User var0 = getUser(user.getUserId());
        Result result = new Result();
        result.setSuccess(false);
        if (var0 != null) {
            if (EncryptUtils.getInstance().SHA1(inputOldPWD, "user").equals(var0.getUserPassword())) {
                result.setSuccess(true);
            } else {
                result.setMessage("旧密码输入错误");
            }
        } else {
            result.setMessage("用户未登录");
        }
        return result;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
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

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public User getUser(String queryStr) {
        User user = null;
        if (RegexUtils.isMobile(queryStr)) {
            user = userMapper.getUserByPhone(queryStr);
        } else if (RegexUtils.isEmail(queryStr)) {
            user = userMapper.getUserByEmail(queryStr);
        } else {
            user = userMapper.getUserByNickname(queryStr);
        }
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public User getUser(long uid) {
        return userMapper.getByPrimaryKey(uid);
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public int register(String nickname, String password, String account, String ipInfo) {
        String headImageUrl = DefaultImageDispatcherUtils.defaultAvatar();
        try {

            /**
             * (String userNickname,
             *             String userPassword,
             *             String userName,
             *             String userGender,
             *             Date userBirthday,
             *             String userPhone,
             *             String userMail,
             *             String userAddress,
             *
             *
             *
             *
             *
             *
             *
             *
             *
             *             int userScore,
             *             int userRank,
             *             String userHeaderImgUrl,
             *             String userDescription,
             *             String userRegisterIp,
             *             String userLastUpdateTime,
             *             Integer userLock,
             *             Integer userFreeze,
             *             Integer userPower,
             *             Integer userLevel,
             *             Integer userVisit)
             */
            User user = new User(nickname, password, null, null, null, null, null, null, 0, 100000, headImageUrl, null, ipInfo, null, UserLock.UNLOCK.getStatus(), UserFreeze.UNFREEZE.getStatus(), UserPower.USER.getLevel(), 0, 0);
            if (RegexUtils.isEmail(account)) {
                user.setUserMail(account);
            } else if (RegexUtils.isMobile(account)) {
                user.setUserPhone(account);
            }
            return userMapper.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int register(User user) {
        String headImageUrl = DefaultImageDispatcherUtils.defaultAvatar();
        try {
            user.setUserScore(0);
            user.setUserRank(1000000);
            user.setUserDescription(null);
            user.setUserHeaderImgUrl(headImageUrl);
            user.setUserLock(UserLock.UNLOCK.getStatus());
            user.setUserFreeze(UserFreeze.UNFREEZE.getStatus());
            user.setUserPower(2);
            user.setUserLevel(0);
            user.setUserVisit(0);
            return userMapper.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public int updateUserInfo(String account, String newPassword) {
        try {
            User user = new User();
            if (RegexUtils.isEmail(account)) {
                user.setUserMail(account);
            } else if (RegexUtils.isPhone(account)) {
                user.setUserPhone(account);
            }
            user.setUserPassword(newPassword);
            return userMapper.updateUserSelective(user);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
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

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
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
