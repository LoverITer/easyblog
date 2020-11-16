package top.easyblog.web.service;

import java.util.List;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/11/09 18:14
 */
public interface HotWordService {

    int addSearchHistoryByUserId(String userId, String searchKey);

    Long delSearchHistoryByUserId(String userId, String searchKey);

    List<String> getSearchHistoryByUserId(String userId);

    int incrementScoreByUserId(String searchKey);

    List<String> getHotList(String searchKey);

    int incrementScore(String searchKey);
}
