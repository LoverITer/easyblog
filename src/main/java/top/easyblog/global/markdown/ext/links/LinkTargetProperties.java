package top.easyblog.global.markdown.ext.links;

import java.util.List;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/10/25 14:13
 */
public class LinkTargetProperties {
    /**
     * 排除添加 target 属性的链接
     */
    private List<String> excludes;
    /**
     * target 属性的值
     */
    private String target = "_target";

    /**
     * 相对路径排除
     */
    private boolean relativeExclude = true;

    public LinkTargetProperties() {
    }

    /**
     *
     * @param excludes    需要排除的路径
     * @param target       targte属性的值
     * @param relativeExclude   是否排除相对路径
     */
    public LinkTargetProperties(List<String> excludes, String target, boolean relativeExclude) {
        this.excludes = excludes;
        this.target = target;
        this.relativeExclude = relativeExclude;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isRelativeExclude() {
        return relativeExclude;
    }

    public void setRelativeExclude(boolean relativeExclude) {
        this.relativeExclude = relativeExclude;
    }
}
