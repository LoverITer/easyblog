package top.easyblog.web.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.easyblog.config.web.WebAjaxResult;
import top.easyblog.entity.po.User;
import top.easyblog.global.enums.UserFreeze;
import top.easyblog.global.enums.UserLock;
import top.easyblog.global.enums.UserPower;
import top.easyblog.mapper.UserMapper;
import top.easyblog.util.EncryptUtils;
import top.easyblog.util.RegexUtils;
import top.easyblog.web.service.IUserService;

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
    public WebAjaxResult isAuthorized(User user, String inputOldPWD) {
        User var0 = getUser(user.getUserId());
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setSuccess(false);
        if (var0 != null) {
            if (EncryptUtils.getInstance().SHA1(inputOldPWD, "user").equals(var0.getUserPassword())) {
                ajaxResult.setSuccess(true);
            } else {
                ajaxResult.setMessage("旧密码输入错误");
            }
        } else {
            ajaxResult.setMessage("用户未登录");
        }
        return ajaxResult;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public WebAjaxResult isNewPasswordSameOldPassword(String inputOldPWD, String newPWD) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        ajaxResult.setSuccess(false);
        if (inputOldPWD.equals(newPWD)) {
            ajaxResult.setSuccess(true);
            ajaxResult.setMessage("新旧密码不能一样");
        }
        return ajaxResult;
    }


    /**
     * 密码要求：
     * <p>
     * 1.长度超过8位
     * 2.包括大小写字母.数字.其它符号,以上四种至少三种
     *
     * @param password 用户密码
     * @return
     */
    @Override
    public WebAjaxResult isPasswordLegal(String password) {
        WebAjaxResult ajaxResult = new WebAjaxResult();
        if (StringUtils.isEmpty(password) || password.length() < 8) {
            ajaxResult.setMessage("密码长度必须大于8位");
        }
        if (verifyPasswordComplexity(password) < 3) {
            ajaxResult.setMessage("密码至少应该包含大小写字母、数字、其他符号中的三种");
        }
        ajaxResult.setSuccess(true);
        return ajaxResult;
    }


    /**
     * 检查密码的复杂度
     * <p>
     * 包括大小写字母.数字.其它符号,以上四种至少三种
     *
     * @param password
     * @return
     */
    private Integer verifyPasswordComplexity(String password) {
        int count = 0;
        //检查是否有大写字母
        if (password.length() - password.replaceAll("[A-Z]", "").length() > 0) {
            count++;
        }
        //检查是否有小写字母
        if (password.length() - password.replaceAll("[a-z]", "").length() > 0) {
            count++;
        }
        //检查是否有数字
        if (password.length() - password.replaceAll("[0-9]", "").length() > 0) {
            count++;
        }
        //检查是否有其它符号
        if (password.replaceAll("[0-9,A-Z,a-z]", "").length() > 0) {
            count++;
        }
        return count;
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
        try {
            User user = new User(nickname, password, null, null, null, null, null, null, 0, 100000, null, null, ipInfo, null, UserLock.UNLOCK.getStatus(), UserFreeze.UNFREEZE.getStatus(), UserPower.USER.getLevel(), 0, 0);
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
        try {
            user.setUserScore(0);
            user.setUserRank(1000000);
            user.setUserDescription(null);
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
    public int registerByThirdPart(User user) {
        try{
            user.setUserFreeze(0);
            user.setUserLock(0);
            user.setUserPower(3);
            user.setUserPassword("");
            return userMapper.saveCoreInfo(user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
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
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public int updateUserInfo(User user) {
        if (Objects.nonNull(user)) {
            try {
                return userMapper.updateByPrimaryKeySelective(user);
            } catch (Exception e) {
                e.printStackTrace();
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
            }
        }
        return 0;
    }

}
