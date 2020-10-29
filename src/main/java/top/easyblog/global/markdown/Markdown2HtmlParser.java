package top.easyblog.global.markdown;

import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import top.easyblog.global.markdown.ext.links.LinkTargetAttributeProvider;
import top.easyblog.global.markdown.ext.links.LinkTargetExtensions;
import top.easyblog.global.markdown.ext.links.LinkTargetProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
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
        options.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),
                TocExtension.create(),
                JekyllTagExtension.create(),
                SimTocExtension.create(),
                EmojiExtension.create(),
                LinkTargetExtensions.create()
        )).set(TocExtension.LEVELS, 255)
                .set(TocExtension.IS_NUMBERED,false)
                .set(TocExtension.IS_TEXT_ONLY,true)
                .set(TocExtension.TITLE, "目录")
                .set(TocExtension.DIV_CLASS, "toc");
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Document document = parser.parse(markdown);
        Consumer<Document> accept=(doc)->
                doc.set(LinkTargetExtensions.LINK_TARGET,new LinkTargetProperties(null, LinkTargetAttributeProvider.TARGET_BLANK,false));
        accept.accept(document);
        return renderer.render(document);
    }


    public static void main(String[] args) throws IOException {
        StringBuilder text = new StringBuilder();
        File file = new File("C:\\Users\\Administrator\\Desktop\\Java面试前需要掌握的知识点\\Redis\\分布式锁方案.md");
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        while (fis.read(bytes) != -1) {
            text.append(new String(bytes));
        }

        String mark="[TOC]\n" +
                "\n" +
                "#  1、马什么梅\n" +
                "## 1.1、马冬梅\n" +
                "### 1.2、马冬什么\n" +
                "#### 1.3、马冬梅\n" +
                "##### 1.4、什么冬梅\n" +
                "###### 1.5、马冬梅";
        System.out.println(new Markdown2HtmlParser().render2Html(mark));
    }

}
