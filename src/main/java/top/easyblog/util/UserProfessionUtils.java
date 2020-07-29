package top.easyblog.util;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangxin
 */
public class UserProfessionUtils {

    private static final Map<String, String> map = new ConcurrentHashMap<>(2 ^ 6);

    static {
        map.put("000000", "");
        map.put("000001", "电子·微电子");
        map.put("000010","通信(设备·运营·增值服务)");
        map.put("000011","批发·零售");
        map.put("000100","贸易·进出口");
        map.put("000101","广告·会展·公关");
        map.put("000110","专业服务(咨询·财会·法律等)");
        map.put("000111","房地产开发·建筑与工程");
        map.put("001000","娱乐·运动·休闲");
        map.put("001001","快速消费品(食品·饮料·日化·烟酒等)");
        map.put("001010","家电业");
        map.put("001011","家居·室内设计·装潢");
        map.put("001100","旅游·酒店·餐饮服务");
        map.put("001101","办公设备·用品");
        map.put("001111","医疗设备·器械");
        map.put("010000","汽车·摩托车(制造·维护·配件·销售·服务)");
        map.put("010001","制药·生物工程");
        map.put("010010","环保");
        map.put("010011","印刷·包装·造纸");
        map.put("010100","中介服务(人才·商标专利)");
        map.put("010101","教育·培训·科研·院校");
        map.put("010110","医疗·保健·美容·卫生服务");
        map.put("010111","互联网·电子商务");
        map.put("011000","仪器·仪表·工业自动化·电气");
        map.put("011001","计算机软件");
        map.put("011010","计算机硬件·网络设备");
        map.put("011011","IT服务·系统集成");
        map.put("011100","银行");
        map.put("011101","保险");
        map.put("011110","基金·证券·期货·投资");
        map.put("011111","耐用消费品(服饰·纺织·家具）");
        map.put("100000","机械制造·机电·重工");
        map.put("100001","原材料及加工（金属·木材·橡胶·塑料·玻璃·陶瓷·建材）");
        map.put("100010","政府·非营利机构");
        map.put("100011","房地产服务(中介·物业管理·监理、设计院)");
        map.put("100100","媒体·出版·文化传播");
        map.put("100101","化工");
        map.put("100110","采掘·冶炼");
        map.put("100111","能源（电力·石油）·水利");
        map.put("101000","软件外包");
        map.put("101001","网络游戏");
        map.put("101010","嵌入式");
        map.put("101011","国防/军队");
        map.put("101100","其他");
    }


    /**
     * 获得用户的行业
     * @param code
     * @return
     */
    public static String getUserProfession(String code){
        if(Objects.nonNull(code)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (code.equals(k)) {
                    return v;
                }
            }
        }
        return "";
    }
}
