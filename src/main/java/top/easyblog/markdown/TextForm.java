package top.easyblog.markdown;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/10 18:31
 */
public enum TextForm {

    HTML("html"),
    TXT("txt"),
    PDF("pdf"),
    DOCX("docx"),
    CSS("css"),
    MARKDOWN("md");

    private String textFrom;

    TextForm(String textFrom) {
        this.textFrom = textFrom;
    }

    public String getTextFrom() {
        return this.textFrom;
    }

    /**
     * 比较两个文本类型是否相同
     *
     * @param textForm
     * @return
     */
    public boolean equalsIgnoreCase(TextForm textForm) {
        return this.getTextFrom().equalsIgnoreCase(textForm.getTextFrom());
    }
}
