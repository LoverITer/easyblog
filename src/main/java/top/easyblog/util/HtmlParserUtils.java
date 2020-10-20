package top.easyblog.util;

import java.util.regex.Pattern;

/** 转化HTML文本为TXT普通文本
 * @author huangxin
 */
public class HtmlParserUtils {
    /**
     * 匹配所有的<script></script>以及里面的内容
     */
    private static final String REG_EX_SCRIPT = "<script[^>]*?>[\\s\\S]*?</script>";
    /**
     * 匹配所有的<style></style>以及里面的内容
     */
    private static final String REG_EX_STYLE = "<style[^>]*?>[\\s\\S]*?</style>";
    /**
     * 匹配替换其他的HTML元素
     */
    private static final String REG_EX_HTML = "<[^>]+>";
    /**
     * 匹配空格回车换行符
     */
    private static final String REG_EX_SPACE = "\\s*|\t|\r|\n";
    /**
     * 匹配所有的<code></code>以及里面的内容
     */
    private static final String REG_EX_PRE = "<code [^>]*?>[\\s\\S]*?</code>";

    /** 过滤掉HTML标签已近特定元素
     * @param htmlText
     * @return 删除Html标签
     */
    private static String delHtmlTag(String htmlText) {
        if (htmlText == null || htmlText.length() == 0) {
            return "";
        }
        htmlText = matcher(REG_EX_PRE, htmlText, Pattern.CASE_INSENSITIVE);
        htmlText = matcher(REG_EX_SCRIPT, htmlText, Pattern.CASE_INSENSITIVE);
        htmlText = matcher(REG_EX_STYLE, htmlText, Pattern.CASE_INSENSITIVE);
        htmlText = matcher(REG_EX_HTML, htmlText, Pattern.CASE_INSENSITIVE);
        htmlText = matcher(REG_EX_SPACE, htmlText, Pattern.CASE_INSENSITIVE);
        return htmlText.trim();
    }

    private static String matcher(String reg, String htmlText, int flags) {
        Pattern pattern = null;
        //过滤<code>标签以及里面的内容
        pattern = Pattern.compile(reg, flags);
        return pattern.matcher(htmlText).replaceAll("");
    }

    /**
     * 转化HTML为text
     *
     * @param htmlStr
     * @return
     */
    public static String HTML2Text(String htmlStr) {
        htmlStr = delHtmlTag(htmlStr);
        htmlStr = htmlStr.replaceAll(" ", "");
        htmlStr = htmlStr.replaceAll("\\[TOC\\]", "");
        return htmlStr;
    }


}
