package top.easyblog.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * 合并同一个类型的两个对象的不同字段生成一个新的同类型对象
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/09 14:05
 */
public class CombineBeans {

    private CombineBeans() {
    }

    /**
     * 该方法是用于相同对象不同属性值的合并，如果两个相同对象中同一属性都有值，
     * 那么sourceBean中的值会覆盖targetBean的值
     *
     * @param sourceBean 字段信息较新的bean
     * @param targetBean 字段信息较老的bean
     * @param <T>        返回类型
     * @return
     */
    public static <T> T combine(T sourceBean, T targetBean) {
        Class sourceClass = Objects.requireNonNull(sourceBean).getClass();
        Class targetClass = Objects.requireNonNull(sourceBean).getClass();

        Field[] sourceFields = sourceClass.getDeclaredFields();
        Field[] targetFields = targetClass.getDeclaredFields();
        for (int i = 0; i < sourceFields.length; i++) {
            Field sourceField = sourceFields[i];
            if (Modifier.isStatic(sourceField.getModifiers())) {
                continue;
            }
            Field targetField = targetFields[i];
            if (Modifier.isStatic(targetField.getModifiers())) {
                continue;
            }
            sourceField.setAccessible(true);
            targetField.setAccessible(true);
            try {
                if (sourceField.get(sourceBean) != null) {
                    targetField.set(targetBean, sourceField.get(sourceBean));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return targetBean;
    }


    /*public static void main(String[] args) {
        User user1 = new User();
        user1.setUserId(1234);
        user1.setUserHeaderImgUrl("24242424242");
        User user2 = new User();
        user2.setUserHeaderImgUrl("eeeeeeee");
        System.out.println(CombineBeans.combine(user1, user2));
    }*/

}
