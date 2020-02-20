package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.bean.UserAccount;
import top.easyblog.mapper.core.BaseMapper;

/**
 * @author HuangXin
 * @since 2020/2/4 23:34
 */
@Repository
public interface UserAccountMapper extends BaseMapper<UserAccount> {
    /**
     *
     * @param account
     * @return
     */
    int updateSelective(UserAccount account);

    /**
     * 通过用户id查询用户的其他账号
     * @param userId
     * @return
     */
    UserAccount getByUserId(@Param("userId") int userId);

}
