package org.easyblog.service;

import org.easyblog.bean.CategoryCare;

import java.util.List;

public interface ICategoryCareService {

    List<CategoryCare>  getCategoryCare(int categoryId);


    /**
     * 添加一条关注信息
     * @param careUserId   关注者的userId
     * @param categoryId   关注的分类Id
     * @return
     */
    int saveCareInfo(int careUserId,int categoryId);


    /**
     * 删除一条记录
     * @param userId
     * @param categoryId
     * @return
     */
    boolean deleteCareInfo(int userId,int categoryId);
}
