package org.easyblog.service.base;

import org.easyblog.bean.UserSigninLog;

public interface IUserSigninLogService {

    /**
     * 保存用户登录记录
     * @param log
     */
    int saveSigninLog(UserSigninLog log);


}
