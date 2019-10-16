package org.easyblog.mapper;

import org.easyblog.bean.Article;
import org.easyblog.mapper.core.BaseMapper;

public interface ArticleMapper extends BaseMapper<Article> {

    int insertSelective(Article record);

    int updateByPrimaryKeySelective(Article record);

    int updateByPrimaryKeyWithBLOBs(Article record);

}