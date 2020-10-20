package top.easyblog.log;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/10/12 18:55
 */
public abstract class AbstractLogBody {

    protected String classMethod;
    protected Object[] args;

    public AbstractLogBody(String classMethod, Object[] args) {
        this.classMethod = classMethod;
        this.args = args;
    }
}
