package top.easyblog.markdown;

/**
 * @author HuangXin
 * @since 2020/1/24 11:59
 */
public class AbstractMarkdownParser implements MarkdownParser {
    /**
     * 将Markdown文本渲染成HTML文本
     *
     * @param markdown markdown文本
     * @return String
     */
    @Override
    public String render2Html(String markdown) {
        return null;
    }

    /**
     * 将Markdown文本渲染成PDF文本
     *
     * @param markdown markdown文本
     * @return String
     */
    @Override
    public String render2PDF(String markdown) {
        return null;
    }

    /**
     * 将Markdown文本渲染成Word文本
     *
     * @param markdown markdown文本
     * @return String
     */
    @Override
    public String render2DOCX(String markdown) {
        return null;
    }
}
