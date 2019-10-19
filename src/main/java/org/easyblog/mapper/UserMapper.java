package org.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.easyblog.bean.User;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {

    User getUserByUserQQAndPassword(@Param("user_qq") String userQQ,@Param("password") String password);

    User getUserByUserPhoneAndPassword(@Param("userPhone") String userPhone,@Param("password")String password);

    User getUserByUserEmailAndPassword(@Param("userEmail") String userEmail,@Param("password")String password);

    User getUserByNickname(@Param("nickname") String nickname);

    User getUserByPhone(@Param("phone") String phone);

    User getUserByEmail(@Param("email") String phone);
}
