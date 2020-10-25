package top.easyblog.global.markdown;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import top.easyblog.global.markdown.ext.links.LinkTargetAttributeProvider;
import top.easyblog.global.markdown.ext.links.LinkTargetExtensions;
import top.easyblog.global.markdown.ext.links.LinkTargetProperties;

import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author HuangXin
 * @since 2020/1/24 12:00
 * Markdown 渲染为HTML实现
 */
public class Markdown2HtmlParser extends AbstractMarkdownParser {

    @Override
    public String render2Html(String markdown) {
        MutableDataSet options=new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        //扩展功能
        options.set(Parser.EXTENSIONS, Collections.singletonList(TablesExtension.create()));
        options.set(Parser.EXTENSIONS,Collections.singleton(LinkTargetExtensions.create()));
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Document document = parser.parse(markdown);
        Consumer<Document> accept=(doc)->
                doc.set(LinkTargetExtensions.LINK_TARGET,new LinkTargetProperties(null, LinkTargetAttributeProvider.TARGET_BLANK,false));
        accept.accept(document);
        return renderer.render(document);
    }


    public static void main(String[] args) {
        String text="String markdown = \"[测试1](http://www.itlangzi.com/test1 '测试1') [测试2](/test2 '测试2') [测试3](https://www.google.com/test3 '测试3')\"";
        System.out.println(new Markdown2HtmlParser().render2Html(text));
    }

}
