package top.easyblog.service;

import top.easyblog.oauth2.enums.AppType;

/**
 * 第三方登录授权后用户信息保存
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/05/29 10:17
 */
public interface IOauthService<T> {

    /**
     * 注册账户
     *
     * @param t 账户实体对象
     * @param userId 本系统用户的Id
     * @return 返回新增的数据库Id
     */
    int register(T t,int userId);

    /**
     * 根据授权的open_id和应用类型查找系统中是否有这个用户
     *
     * @param openId    open_id
     * @param appType  app_type, 比如 : QQ、GitHub
     * @return   T
     */
    T getUserByOpenIdAndAppType(String openId, AppType appType);



}
