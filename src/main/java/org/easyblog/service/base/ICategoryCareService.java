package org.easyblog.service.base;

import org.easyblog.bean.CategoryCare;

import java.util.List;

public interface ICategoryCareService {

    List<CategoryCare>  getCategoryCare(int categoryId);


    int saveCareInfo(int careUserId,int categoryId);


    /**
     * 删除一条记录
     * @param userId
     * @param categoryId
     * @return
     */
    boolean deleteCareInfo(int userId,int categoryId);
}
