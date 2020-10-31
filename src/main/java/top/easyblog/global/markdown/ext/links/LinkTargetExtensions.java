package top.easyblog.global.markdown.ext.links;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import org.jetbrains.annotations.NotNull;

/**
 * 自定义的Provider需要通过 HtmlRenderer.Builder.attributeProviderFactory 的方式注册才能使用
 * @author ：huangxin
 * @modified ：
 * @since ：2020/10/25 14:17
 */
public class LinkTargetExtensions implements HtmlRenderer.HtmlRendererExtension {

    // 定义配置参数
    // 并设置默认值
    public static final DataKey<LinkTargetProperties> LINK_TARGET = new DataKey<>("LINK_TARGET", new LinkTargetProperties());


    @Override
    public void rendererOptions(@NotNull MutableDataHolder mutableDataHolder) {

    }

    @Override
    public void extend(HtmlRenderer.@NotNull Builder builder, @NotNull String s) {
        builder.attributeProviderFactory(LinkTargetAttributeProvider.factory());
    }

    public static LinkTargetExtensions create() {
        return new LinkTargetExtensions();
    }
}
