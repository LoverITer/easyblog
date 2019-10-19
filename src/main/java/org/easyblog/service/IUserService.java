package org.easyblog.service;

import org.easyblog.bean.User;

public interface IUserService {

    /**
     * 根据用户名和密码获得用户
     * @param username
     * @param password
     * @return
     */
    User checkUser(String username,String password);


    /**
     * 根据昵称获得用户
     * @param queryStr
     * @return
     */
    User getUser(String queryStr);


    /**
     * 新增一条用户信息
     * @param nickname 昵称
     * @param password  密码
     * @param account   账号（登录账号）
     * @param ipInfo  ip地址信息
     * @return
     */
    boolean register(String nickname, String password, String account, String ipInfo);



}
