package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.entity.po.UserAccount;
import top.easyblog.mapper.core.BaseMapper;

/**
 * @author HuangXin
 * @since 2020/2/4 23:34
 */
@Repository
public interface UserAccountMapper extends BaseMapper<UserAccount> {
    /**
     * 选择性更新用户的其他战账户信息
     *
     * @param account
     * @return
     */
    int updateSelective(UserAccount account);

    /**
     * 通过用户id查询用户的其他账号
     *
     * @param accountUser
     * @return
     */
    UserAccount getByUserId(@Param("accountUser") int accountUser);

}
