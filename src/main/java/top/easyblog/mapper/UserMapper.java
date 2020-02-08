package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.bean.User;
import top.easyblog.mapper.core.BaseMapper;

@Repository
public interface UserMapper extends BaseMapper<User> {

    User getUserByUserPhoneAndPassword(@Param("userPhone") String userPhone, @Param("password") String password);

    User getUserByUserEmailAndPassword(@Param("userEmail") String userEmail, @Param("password") String password);

    User getUserByNickname(@Param("nickname") String nickname);

    User getUserByPhone(@Param("phone") String phone);

    User getUserByEmail(@Param("email") String email);

    int updateUserSelective(User user);

    int updateByPrimaryKeySelective(@Param("user") User user);

}
