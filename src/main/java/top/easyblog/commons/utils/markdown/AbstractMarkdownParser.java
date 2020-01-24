package top.easyblog.commons.utils.markdown;

/**
 * @author HuangXin
 * @since 2020/1/24 11:59
 */
public class AbstractMarkdownParser implements MarkdownParser {
    @Override
    public String render2Html(String markdown) {
        return null;
    }

    @Override
    public String render2PDF(String markdown) {
        return null;
    }

    @Override
    public String render2DOCX(String markdown) {
        return null;
    }
}
