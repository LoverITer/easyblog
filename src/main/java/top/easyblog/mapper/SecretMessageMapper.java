package top.easyblog.mapper;

import org.springframework.stereotype.Repository;
import top.easyblog.bean.SecretMessage;
import top.easyblog.mapper.core.BaseMapper;

@Repository
public interface SecretMessageMapper extends BaseMapper<SecretMessage> {

    SecretMessage getByPrimaryKey(SecretMessage secretMessage);

    int saveSelective(SecretMessage record);

    int updateByPrimaryKeySelective(SecretMessage record);

    int updateByPrimaryKeyWithContent(SecretMessage record);
}