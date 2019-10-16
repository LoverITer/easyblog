package org.easyblog.mapper;

import org.easyblog.bean.SecretMessage;

public interface SecretMessageMapper {
    int deleteByPrimaryKey(SecretMessage key);

    int insert(SecretMessage record);

    int insertSelective(SecretMessage record);

    SecretMessage selectByPrimaryKey(SecretMessage key);

    int updateByPrimaryKeySelective(SecretMessage record);

    int updateByPrimaryKeyWithBLOBs(SecretMessage record);

    int updateByPrimaryKey(SecretMessage record);
}