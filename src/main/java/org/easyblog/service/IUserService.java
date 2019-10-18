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


}
