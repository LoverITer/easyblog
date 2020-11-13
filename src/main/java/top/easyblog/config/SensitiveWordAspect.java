package top.easyblog.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import top.easyblog.util.SensitiveWordUtils;

/**
 * 敏感词过滤器：对标注@SensitiveFilter的类或方法进行敏感词过滤
 * 1. 涉政文字：涉及政人物、政治事件、宗教、反动分裂，以及恐怖主义等违规文本
 * 2. 色情文字：识别淫秽、污秽、色诱、文爱等涉黄内容，支持重度色情、轻度色情分级
 * 3. 辱骂文本：识别各类场景中含有污辱、谩骂、诋毁等辱骂内容
 * 4. 违禁文本：识别赌博、刀枪、毒品、造假、贩假等违规内容
 * 5. 广告导流：识别利用微信号、手机号、QQ、二维码等开展违法垃圾广告内容
 * 6. 垃圾内容：识别水贴、刷屏、无意义等垃圾内容，实现智能反垃圾
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/11/10 13:39
 */
@Slf4j
@Aspect
public class SensitiveWordAspect {

    private SensitiveWordUtils sensitiveWordUtils = SensitiveWordUtils.getInstance();



}
