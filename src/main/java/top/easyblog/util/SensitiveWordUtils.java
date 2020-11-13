package top.easyblog.util;

import top.easyblog.config.SensitiveWordInit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 敏感词过滤器：利用DFA算法  进行敏感词过滤
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/11/09 19:04
 */
public class SensitiveWordUtils {

    /***敏感词过滤器：利用DFA算法  进行敏感词过滤*/
    private Map sensitiveWordMap = null;

    /**
     * 匹配规则
     */
    public enum MatchType {
        /***最小匹配规则*/
        MIN_MATCH_TYPE(1),
        /***最大匹配规则*/
        MAX_MATCH_TYPE(2);

        private int matchType;

        MatchType(int matchType) {
            this.matchType = matchType;
        }

        public int getMatchType() {
            return matchType;
        }
    }

    /***单例对象*/
    private static SensitiveWordUtils instance = null;

    /***构造函数，初始化敏感词库*/
    private SensitiveWordUtils() {
        sensitiveWordMap = new SensitiveWordInit().init();
    }

    /**
     * 获取单例
     **/
    public static SensitiveWordUtils getInstance() {
        if (null == instance) {
            synchronized (SensitiveWordUtils.class) {
                if (null == instance) {
                    instance = new SensitiveWordUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 获取文字中的敏感词
     *
     * @param txt       原始文本
     * @param matchType 匹配规则
     * @return
     */
    public Set<String> getSensitiveWord(String txt, MatchType matchType) {
        Set<String> sensitiveWordList = new HashSet<>();
        for (int i = 0; i < txt.length(); i++) {
            // 判断是否包含敏感字符
            int length = CheckSensitiveWord(txt, i, matchType);
            // 存在,加入list中
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                // 减1的原因，是因为for会自增
                i = i + length - 1;
            }
        }
        return sensitiveWordList;
    }

    /**
     * 替换敏感字字符
     *
     * @param txt
     * @param matchType
     * @param replaceChar
     * @return
     */
    public String replaceSensitiveWord(String txt, String replaceChar, MatchType matchType) {
        String resultTxt = txt;
        // 获取所有的敏感词
        Set<String> set = getSensitiveWord(txt, matchType);
        Iterator<String> iterator = set.iterator();
        String word = null;
        String replaceString = null;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }
        return resultTxt;
    }

    /**
     * 获取替换字符串
     *
     * @param replaceChar
     * @param length
     * @return
     */
    private String getReplaceChars(String replaceChar, int length) {
        String resultReplace = replaceChar;
        for (int i = 1; i < length; i++) {
            resultReplace += replaceChar;
        }
        return resultReplace;
    }

    /**
     * 检查文字中是否包含敏感字符，检查规则如下：<br>
     * 如果存在，则返回敏感词字符的长度，不存在返回0
     *
     * @param txt
     * @param beginIndex
     * @param matchType
     * @return
     */
    public int CheckSensitiveWord(String txt, int beginIndex, MatchType matchType) {
        // 敏感词结束标识位：用于敏感词只有1位的情况
        boolean flag = false;
        // 匹配标识数默认为0
        int matchFlag = 0;
        Map nowMap = sensitiveWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            char word = txt.charAt(i);
            // 获取指定key
            nowMap = (Map) nowMap.get(word);
            // 存在，则判断是否为最后一个
            if (nowMap != null) {
                // 找到相应key，匹配标识+1
                matchFlag++;
                // 如果为最后一个匹配规则,结束循环，返回匹配标识数
                if ("1".equals(nowMap.get("isEnd"))) {
                    // 结束标志位为true
                    flag = true;
                    // 最小规则，直接返回,最大规则还需继续查找
                    if (MatchType.MIN_MATCH_TYPE == matchType) {
                        break;
                    }
                }
            }
            // 不存在，直接返回
            else {
                break;
            }
        }

        if (MatchType.MAX_MATCH_TYPE == matchType) {
            if (matchFlag < 2 || !flag) {
                //长度必须大于等于1，为词
                matchFlag = 0;
            }
        }
        if (MatchType.MIN_MATCH_TYPE == matchType) {
            if (matchFlag < 2 && !flag) {
                //长度必须大于等于1，为词
                matchFlag = 0;
            }
        }
        return matchFlag;
    }
}
