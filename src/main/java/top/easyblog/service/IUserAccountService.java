package top.easyblog.service;

import top.easyblog.entity.po.UserAccount;

/**
 * @author HuangXin
 * @since 2020/2/5 0:07
 */
public interface IUserAccountService {

    /**
     * 判断UserAccount是否存在
     *
     * @param userId 用户Id
     * @return boolean
     */
    boolean isAccountEmpty(int userId);

    /**
     * 创建一个UserAccount
     *
     * @param account UserAccount对象
     * @return int
     */
    int createAccount(UserAccount account);

    /**
     * 根据userId更新用户的账户信息
     *
     * @param account UserAccount对象
     * @return int
     */
    int updateAccountByUserId(UserAccount account);


    /**
     * 根据userId查询用户的账户信息
     *
     * @param userId 用户Id
     * @return UserAccount
     */
    UserAccount getAccountByUserId(int userId);


}
