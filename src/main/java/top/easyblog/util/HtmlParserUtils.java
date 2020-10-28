package top.easyblog.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.*;
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

    private static final Map<String,Integer> H_TAG_LEVEL_MAP =new HashMap<>(16);

    static {
        H_TAG_LEVEL_MAP.put("h1",6);
        H_TAG_LEVEL_MAP.put("h2",5);
        H_TAG_LEVEL_MAP.put("h3",4);
        H_TAG_LEVEL_MAP.put("h4",3);
        H_TAG_LEVEL_MAP.put("h5",2);
        H_TAG_LEVEL_MAP.put("h6",1);
    }

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

    /**
     * 解析一段HTML文本中的<code><h><code/>标签,形成目录级别:
     * h3---------
     * |-h4----------
     * |-h4----------
     * |----h5--------
     * |_h4----------
     * h3-----------
     * |____h4-------
     * | _h4-------
     * @param content
     * @return
     */
    public static List<List<String>> parseHTag(String content){
        if(StringUtils.isEmpty(content)){
            return null;
        }
        List<List<String>> tableContentLists=new ArrayList<>();
        Document document = Jsoup.parseBodyFragment(content);
        Elements elements = document.getAllElements();
        String lastTag="h6";
        Deque<String> queue=new ArrayDeque<>();
        ArrayList<Element> hTag = new ArrayList<>();
        for(Element node:elements){
            String nodeName = node.nodeName();
            if("h1".equals(nodeName)|| "h2".equals(nodeName)|| "h3".equals(nodeName)||
                    "h4".equals(nodeName)|| "h5".equals(nodeName)|| "h6".equals(nodeName)){
                hTag.add(node);
            }
        }
        for(int i=0;i<hTag.size();i++){
            queue.offer(hTag.get(i).html());
            if(H_TAG_LEVEL_MAP.get(lastTag)<= H_TAG_LEVEL_MAP.get(hTag.get(i).nodeName())){
                lastTag=hTag.get(i).nodeName();
            }
            if(i==hTag.size()-1|| H_TAG_LEVEL_MAP.get(hTag.get(i+1).nodeName())>= H_TAG_LEVEL_MAP.get(lastTag)){
                ArrayList<String> list = new ArrayList<>();
                int size=queue.size();
                for(int j=0;j<size;j++){
                    list.add(queue.pop());
                }
                tableContentLists.add(list);
            }
        }

        return tableContentLists;
    }


}
