package org.easyblog.mapper;

import org.easyblog.bean.UserSigninLog;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSigninLogMapper extends BaseMapper<UserSigninLog> {

    int saveSelective(UserSigninLog record);

    int updateByPrimaryKeySelective(UserSigninLog record);


    /**
     * 获得指定数量的某个用户的登录日志（按时间降序）
     * @param userId
     * @param num
     * @return
     */
    List<UserSigninLog> getUserLoginInfo(int userId,int num);

}