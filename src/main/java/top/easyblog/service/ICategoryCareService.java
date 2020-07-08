package top.easyblog.service;

import top.easyblog.entity.po.CategoryCare;

import java.util.List;

/**
 * @author huanxin
 */
public interface ICategoryCareService {

    /**
     * 根据CategoryCare Id获取CategoryCare
     *
     * @param categoryId CategoryCare Id
     * @return List
     */
    List<CategoryCare> getCategoryCare(int categoryId);


    /**
     * 添加一条关注信息
     *
     * @param careUserId 关注者的userId
     * @param categoryId 关注的分类Id
     * @return int
     */
    int saveCareInfo(int careUserId, int categoryId);


    /**
     * 删除一条记录
     *
     * @param userId
     * @param categoryId
     * @return boolean
     */
    boolean deleteCareInfo(int userId, int categoryId);
}
