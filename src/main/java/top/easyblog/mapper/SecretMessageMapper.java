package top.easyblog.mapper;

import org.springframework.stereotype.Repository;
import top.easyblog.entity.po.SecretMessage;
import top.easyblog.mapper.core.BaseMapper;

/**
 * @author huangxin
 */
@Repository
public interface SecretMessageMapper extends BaseMapper<SecretMessage> {
    /**
     *
     * @param secretMessage
     * @return
     */
    SecretMessage getByPrimaryKey(SecretMessage secretMessage);

    /**
     *
     * @param record
     * @return
     */
    int saveSelective(SecretMessage record);

    /**
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(SecretMessage record);

    /**
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeyWithContent(SecretMessage record);
}