package top.easyblog.util;

import java.util.regex.Pattern;

/**
 * @author huangxin
 */
public class HtmlParserUtils {
    /**
     * 匹配所有的<script></script>以及里面的内容
     */
    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?</script>";
    /**
     * 匹配所有的<style></style>以及里面的内容
     */
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?</style>";
    /**
     * 匹配替换其他的HTML元素
     */
    private static final String regEx_html = "<[^>]+>";
    /**
     * 匹配空格回车换行符
     */
    private static final String regEx_space = "\\s*|\t|\r|\n";
    /**
     * 匹配所有的<code></code>以及里面的内容
     */
    private static final String regEx_pre = "<code [^>]*?>[\\s\\S]*?</code>";

    /**
     * @param htmlStr
     * @return 删除Html标签
     */
    public static String delHTMLTag(String htmlStr) {
        //过滤<code>标签以及里面的内容
        htmlStr = Pattern.compile(regEx_pre, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
        // 过滤script标签
        htmlStr = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
        // 过滤style标签
        htmlStr = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
        // 过滤html标签
        htmlStr = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
        // 过滤空格回车标签
        htmlStr = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE).matcher(htmlStr).replaceAll("");
        return htmlStr.trim();
    }

    /**
     * 转化HTML为text
     *
     * @param htmlStr
     * @return
     */
    public static String HTML2Text(String htmlStr) {
        htmlStr = delHTMLTag(htmlStr);
        htmlStr = htmlStr.replaceAll(" ", "");
        htmlStr = htmlStr.replaceAll("\\[TOC\\]", "");
        return htmlStr;
    }


}
