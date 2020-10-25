package top.easyblog.global.markdown.ext.links;

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.AttributeProviderFactory;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.html.Attribute;
import com.vladsch.flexmark.util.html.MutableAttributes;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import top.easyblog.util.NetWorkUtils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 扩展 flexmark-java 主要是通过实现 AttributeProvider 进行修改
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/10/25 14:10
 */
public class LinkTargetAttributeProvider implements AttributeProvider {

    /**
     * 用于获取配置的数据
     */
    private final DataHolder holder;

    /**
     * 绝对路径正则匹配
     */
    private final Pattern pattern = Pattern.compile("^[a-zA-z]+://[^\\s]*");

    /**
     * target标签的可选值
     */
    public static final String TARGET_BLANK="_blank";
    public static final String TARGET_SELF="_self";
    public static final String TARGET_TOP="_top";
    public static final String TARGET_PARENT="_parent";

    public LinkTargetAttributeProvider(DataHolder holder) {
        this.holder = holder;
    }

    @Override
    public void setAttributes(@NotNull Node node, @NotNull AttributablePart attributablePart, @NotNull MutableAttributes mutableAttributes) {
        //只处理链接
        if (node instanceof Link && attributablePart == AttributablePart.LINK) {
            //获取href标签
            Attribute href = mutableAttributes.get("href");
            if (href == null) {
                return;
            }
            String hrefValue = href.getValue();
            if (StringUtils.isEmpty(hrefValue)) {
                return;
            }
            LinkTargetProperties dataKey = LinkTargetExtensions.LINK_TARGET.get(this.holder);
            // 判断是否是绝对路径
            if (!pattern.matcher(hrefValue).matches()) {
                if (dataKey.isRelativeExclude()) {
                    // 如果是相对路径，则排除
                    return;
                }
            } else {

                // 获取域名/host
                Optional<String> host = NetWorkUtils.getHost(href);
                if (!host.isPresent()) {
                    return;
                }
                List<String> excludes = dataKey.getExcludes();
                if (excludes != null && !excludes.isEmpty()) {

                    // 如果包含当前的host则排除
                    if (excludes.contains(host.get())) {
                        return;
                    }
                }
            }
            String target = dataKey.getTarget();
            if (StringUtils.isEmpty(target)) {
                target = "";
            }
            // 设置target 属性
            mutableAttributes.replaceValue("target", target);
        }
    }

    static AttributeProviderFactory factory() {
        return new IndependentAttributeProviderFactory() {
            @Override
            public @NotNull AttributeProvider apply(@NotNull LinkResolverContext linkResolverContext) {
                // 在此处获取dataHolder
                return new LinkTargetAttributeProvider(linkResolverContext.getOptions());
            }
        };
    }

}
