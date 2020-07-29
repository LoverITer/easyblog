package top.easyblog.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合工具类
 *
 * @author HuangXin
 * @since 2020/1/13 22:53
 */
public class CollectionUtils {

    /**
     * 把List均分成等长的len部分
     *
     * @param sourceList 源List
     * @param len        个数
     * @return
     */
    public static List<List<?>> splitList(List<?> sourceList, int len) {
        if (sourceList == null || sourceList.size() == 0 || len < 1) {
            return null;
        }
        List<List<?>> result = new ArrayList<List<?>>();
        int size = sourceList.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<?> subList = sourceList.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }

    /**
     * 把List分割成不同长度的几个部分
     *
     * @param sourceList    源List
     * @param splitCapacity 分割参数
     * @return
     */
    public static List<List<?>> splitList(List<?> sourceList, int[] splitCapacity) {
        if (sourceList == null || sourceList.size() == 0 || splitCapacity == null || splitCapacity.length < 1) {
           return null;
        }
        int size = 0;
        for (int i = 0; i < splitCapacity.length; i++) {
            size += splitCapacity[i];
        }
        //传入的list长度小于规定的长度
        if (size > sourceList.size()) {
            throw new RuntimeException("不合理的分割参数");
        }
        List<List<?>> result = new ArrayList<>();
        for (int i = 0, count = splitCapacity.length, j = 0; i < count && j < sourceList.size(); i++) {
            List<?> subList = sourceList.subList(j, j + splitCapacity[i]);
            result.add(subList);
            //偏移splitCapacity[i]
            j += splitCapacity[i];
        }
        return result;
    }


}
