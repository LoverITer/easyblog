package top.easyblog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import top.easyblog.entity.po.UserAccount;
import top.easyblog.mapper.UserAccountMapper;
import top.easyblog.service.IUserAccountService;

import java.util.Objects;

/**
 * @author HuangXin
 * @since 2020/2/5 0:12
 */
@Service
public class UserAccountImpl implements IUserAccountService {

    private final UserAccountMapper userAccountMapper;


    public UserAccountImpl(UserAccountMapper userAccountMapper) {
        this.userAccountMapper = userAccountMapper;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Override
    public boolean isAccountEmpty(int userId) {
        if (userId > 0) {
            try {
                UserAccount account = userAccountMapper.getByUserId(userId);
                return account == null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Override
    public int createAccount(UserAccount account) {
        if(account!=null){
            try{
                return userAccountMapper.save(account);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Override
    public int updateAccountByUserId(UserAccount account) {
        if(Objects.nonNull(account)){
            try{

                return userAccountMapper.updateSelective(account);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,rollbackFor=Exception.class)
    @Override
    public UserAccount getAccountByUserId(int userId) {
        UserAccount account=null;
        if(userId>0){
            try {
                account = userAccountMapper.getByUserId(userId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            throw new IllegalArgumentException("错误的查询参数：userId="+userId+"，必须大于0");
        }
        return account;
    }
}
