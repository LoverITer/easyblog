package top.easyblog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import top.easyblog.service.ISeoService;

import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

/**
 * 站点地图生成控制类
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/06/06 08:19
 */
@Slf4j
@Controller
public class SiteMapController {

    @Autowired
    private ISeoService seoService;

    @GetMapping(value = {"/sitemap.xml","baidusitemap.xml"})
    public void generateSiteMap(HttpServletResponse response) throws Exception{
        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        Writer writer = response.getWriter();
        writer.append(seoService.createSiteMapXmlContent());
    }

}
