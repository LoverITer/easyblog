package top.easyblog.service;

import top.easyblog.bean.UserAccount;

/**
 * @author HuangXin
 * @since 2020/2/5 0:07
 */
public interface IUserAccountService {

    public boolean isAccountEmpty(int userId);

    public int createAccount(UserAccount account);

    public int updateAccountByUserId(UserAccount account);

    public UserAccount getAccountByUserId(int userId);


}
