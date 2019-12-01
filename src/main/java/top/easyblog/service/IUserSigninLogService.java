package top.easyblog.service;

import top.easyblog.bean.UserSigninLog;

import java.util.List;

public interface IUserSigninLogService {

    /**
     * 保存用户登录记录
     * @param log
     */
    int saveSigninLog(UserSigninLog log);


    List<UserSigninLog> getUserLoginInfo(int userId, int num);


}
