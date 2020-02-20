package top.easyblog.commons.pagehelper;

import java.util.Objects;

/**
 * @author huangxin
 */
public class PageParam {

    private Integer page;
    private Integer pageSize;

    public PageParam(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
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
