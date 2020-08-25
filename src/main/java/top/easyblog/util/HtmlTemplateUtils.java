package top.easyblog.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import top.easyblog.common.enums.HtmlTemplate;

import java.util.Map;

/**
 * Html渲染模板
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/08/22 21:04
 */
public class HtmlTemplateUtils {

    private static final TemplateEngine engine=new TemplateEngine();


    /**
     * 使用 Thymeleaf 渲染 HTML,输出渲染后的HTML文本段
     *
     * @param template HTML模板
     * @param params   参数
     * @return 渲染后的HTML
     */
    public static String render(HtmlTemplate template, Map<String, Object> params) {
        Context context = new Context();
        context.setVariables(params);
        return engine.process(template.getTemplate(), context);
    }


}
