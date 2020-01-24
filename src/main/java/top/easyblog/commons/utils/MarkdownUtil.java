package top.easyblog.commons.utils;

import top.easyblog.markdown.Markdown2HTMLParser;
import top.easyblog.markdown.MarkdownParser;

/**
 * Markdown文本转成HTML文本的工具类
 */
public class MarkdownUtil {

/*    *//**
     * markdown格式转换成HTML格式
     *
     * @param markdown
     * @return
     *//*
    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    *//**
     * 增加扩展[标题锚点，表格生成]
     * Markdown转换成HTML
     *
     * @param markdown
     * @return
     *//*
    public static String markdownToHtmlExtensions(String markdown) {
        //h标题生成id
        Set<Extension> headingAnchorExtensions = Collections.singleton(HeadingAnchorExtension.create());
        //转换table的HTML
        List<Extension> tableExtension = Collections.singletonList(TablesExtension.create());

        Parser parser = Parser.builder()
                .extensions(tableExtension)
                .build();
        markdown = markdown.replaceAll("\\[TOC\\]", "");
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(headingAnchorExtensions)
                .extensions(tableExtension)
                .attributeProviderFactory(context -> new CustomAttributeProvider())
                .build();
        return renderer.render(document);
    }

    *//**
     * 处理标签的属性
     *//*
    static class CustomAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
            //改变a标签的target属性为_blank
            if (node instanceof Link) {
                attributes.put("target", "_blank");
            }
            if (node instanceof TableBlock) {
                attributes.put("class", "ui celled table");
            }
        }
    }*/

    /**
     * 增加扩展[标题锚点，表格生成]
     * Markdown转换成HTML
     *
     * @param markdown  markdown文本
     * @return  HTML文本
     */
    public static String markdownToHtmlExtensions(String markdown){
        MarkdownParser parser=new Markdown2HTMLParser();
        return parser.render2Html(markdown);
    }

}
