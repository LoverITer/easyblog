package top.easyblog.markdown;

/**
 * @author HuangXin
 * @since 2020/1/24 11:42
 */
public interface MarkdownParser {

    /**
     * 将markdown渲染为html
     *
     * @param markdown markdown文本
     * @return String
     */
    String render2Html(String markdown);


    /**
     * 将markdown渲染为PDF
     *
     * @param markdown markdown文本
     * @return String
     */
    String render2PDF(String markdown);



    /**
     * 将markdown渲染为PDF
     *
     * @param markdown markdown文本
     * @return String
     */
    String render2DOCX(String markdown);

}
