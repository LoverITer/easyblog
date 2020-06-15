package top.easyblog.service;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/06/06 08:26
 */
public interface ISeoService {
    /**
     * 生成站点地图内容
     *
     * @return 站点地图内容
     */
    String createSiteMapXmlContent();
}
