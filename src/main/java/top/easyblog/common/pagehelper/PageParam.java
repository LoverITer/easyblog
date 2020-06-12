package top.easyblog.common.pagehelper;

import lombok.Data;

import java.util.Objects;

/**
 * 分页参数设置
 * @author huangxin
 */
@Data
public class PageParam {

    /**
     * 分页页数
     */
    private Integer page;
    /**
     * 分页大小
     */
    private Integer pageSize;

    public PageParam(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }


    public void setPageSize(Integer pageSize) {
        if(Objects.isNull(pageSize)){
            //默认每页的大小是15条数据
            this.pageSize=PageSize.DEFAULT_PAGE_SIZE.getPageSize();
        }else {
            this.pageSize = pageSize;
        }
    }
}
