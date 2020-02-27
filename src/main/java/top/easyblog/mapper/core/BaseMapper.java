package top.easyblog.mapper.core;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Huangxin
 */
public interface BaseMapper<T> {

    /**
     * 添加一条记录
     * @param t  插入对记录
     * @return    自增主键Id
     */
    Integer save(T t) ;


    /**
     * 通过主键删除
     * @param key  指定主键
     * @return   返回删除的记录的id
     */
    int deleteByPrimaryKey(Long key);

    /**
     * 有条件的删除
     * @param condition  一条记录
     * @return  返回被成功删除的记录的id
     */
    int deleteSelective(T condition);

    /**
     * 根据主键删除
     * @param record  主键
     * @return  返回被成功修改的记录的id
     */
    int updateByPrimaryKey(T record);


    /**
     * 根据主键的得到一个
     * @param id  主键
     * @return 返回一条记录
     */
    T getByPrimaryKey(@Param("id") Long id);


    /**
     * 查询表中所有的记录
     * @return   返回表中所有对记录
     */
    List<T> getAll();

    /**
     * 统计表中某一条件下记录的条数
     * @return   返回记录的条数
     */
    int count();


}
