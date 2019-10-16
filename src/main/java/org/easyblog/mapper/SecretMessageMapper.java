package org.easyblog.mapper;

import org.easyblog.bean.SecretMessage;
import org.easyblog.mapper.core.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretMessageMapper extends BaseMapper<SecretMessage> {

    SecretMessage getByPrimaryKey(SecretMessage secretMessage);

    int saveSelective(SecretMessage record);

    int updateByPrimaryKeySelective(SecretMessage record);

    int updateByPrimaryKeyWithContent(SecretMessage record);
}