package org.easyblog.service;

import org.easyblog.bean.User;
import org.easyblog.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements IUserService{

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public User checkUser(String username, String password) {
        User user =userMapper.getUserByUserEmailAndPassword(username,password);
        if(Objects.isNull(user)){
            user=userMapper.getUserByUserPhoneAndPassword(username,password);
        }
        if(Objects.isNull(user)){
            user=userMapper.getUserByUserQQAndPassword(username,password);
        }
        return user;
    }
}
