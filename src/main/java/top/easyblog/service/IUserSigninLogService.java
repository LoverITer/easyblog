package top.easyblog.service;

import top.easyblog.entity.po.UserSigninLog;

import java.util.List;

/**
 * @author huanxin
 */
public interface IUserSigninLogService {

    /**
     * 保存用户登录记录
     *
     * @param userSigninLog userSigninLog对象
     * @return int
     */
    int saveSigninLog(UserSigninLog userSigninLog);


    /**
     * 得到用户所有的登录记录
     *
     * @param userId 用户ID
     * @param size   查询的登录记录数量
     * @return List
     */
    List<UserSigninLog> getUserLoginInfo(int userId, int size);


}
