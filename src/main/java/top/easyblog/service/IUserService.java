package top.easyblog.service;

import top.easyblog.bean.User;

/**
 * @author huanxin
 */
public interface IUserService {

    /**
     * 根据用户名和密码获得用户
     *
     * @param username 用户账号
     * @param password 密码
     * @return User
     */
    User checkUser(String username, String password);


    /**
     * 根据昵称获得用户
     *
     * @param queryStr 昵称
     * @return User
     */
    User getUser(String queryStr);


    /**
     * 根据id获得用户
     *
     * @param uid 用户Id
     * @return User
     */
    User getUser(long uid);


    /**
     * 新增一条用户信息
     *
     * @param nickname 昵称
     * @param password 密码
     * @param account  账号（登录账号）
     * @param ipInfo   ip地址信息
     * @return int
     */
    int register(String nickname, String password, String account, String ipInfo);

    /**
     * 根据用户Id更新用户密码
     *
     * @param account     账户
     * @param newPassword 新密码
     * @return int
     */
    int updateUserInfo(String account, String newPassword);


    /**
     * 根据用户Id更新用户信息
     *
     * @param user 更新的user对象
     * @return int
     */
    int updateUserInfo(User user);

    /**
     * 根据用户Id删除用户
     *
     * @param userId 用户Id
     * @return int
     */
    int deleteUserByPK(int userId);
}
