package top.easyblog.util;


import top.easyblog.markdown.Markdown2HtmlParser;
import top.easyblog.markdown.MarkdownParser;

/**
 * Markdown文本转成HTML文本的工具类
 * @author huangxin
 */
public class MarkdownUtils {

    /**
     * 增加扩展[标题锚点，表格生成]
     * Markdown转换成HTML
     *
     * @param markdown  markdown文本
     * @return  HTML文本
     */
    public static String markdownToHtmlExtensions(String markdown){
        MarkdownParser parser=new Markdown2HtmlParser();
        return parser.render2Html(markdown);
    }

}
