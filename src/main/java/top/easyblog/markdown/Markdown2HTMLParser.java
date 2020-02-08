package top.easyblog.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;

/**
 * @author HuangXin
 * @since 2020/1/24 12:00
 * Markdown 渲染为HTML的具体实现
 */
public class Markdown2HTMLParser extends AbstractMarkdownParser {
    @Override
    public String render2Html(String markdown) {
        DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
                Extensions.ALL
        );


        Parser parser = Parser.builder(OPTIONS).build();
        HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS)
                .build();

        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        System.out.println(html);
        return html;
    }

}
