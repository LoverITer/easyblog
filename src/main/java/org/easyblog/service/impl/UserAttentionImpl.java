package org.easyblog.service.impl;

import org.easyblog.bean.User;
import org.easyblog.bean.UserAttention;
import org.easyblog.mapper.UserAttentionMapper;
import org.easyblog.service.IUserAttention;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Service
public class UserAttentionImpl implements IUserAttention {


    private final UserAttentionMapper userAttentionMapper;
    private final UserServiceImpl userService;

    public UserAttentionImpl(UserAttentionMapper userAttentionMapper, UserServiceImpl userService) {
        this.userAttentionMapper = userAttentionMapper;
        this.userService = userService;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Cacheable(cacheNames = "attentionInfos", condition = "#result!=null&&#result.size()>0")
    @Override
    public List<UserAttention> getAllUserAttentionInfo(UserAttention userAttention) {
        if (Objects.nonNull(userAttention)) {
            try {
                List<UserAttention> userAttentionInfos = userAttentionMapper.getUserAllAttentionInfoSelective(userAttention);
                if(userAttention.getUserId()!=null) {
                    userAttentionInfos.forEach(ele -> {
                        HashMap<String, User> map = new HashMap<>();
                        User user = userService.getUser(ele.getAttentionId());
                        map.put("user",user);
                        ele.setUserInfo(map);
                    });
                }else if (userAttention.getAttentionId()!=null){
                    userAttentionInfos.forEach(ele -> {
                        HashMap<String, User> map = new HashMap<>();
                        User user = userService.getUser(ele.getUserId());
                        map.put("user",user);
                        ele.setUserInfo(map);
                    });
                }
                return userAttentionInfos;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    @Override
    public int deleteByPK(int id) {
        if(id>0){
            try{
               return userAttentionMapper.deleteByPrimaryKey((long) id);
            }catch (Exception ex){
                ex.printStackTrace();
                return 0;
            }
        }
        return 0;
    }
}
