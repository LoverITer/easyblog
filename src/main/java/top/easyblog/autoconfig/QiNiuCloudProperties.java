package top.easyblog.autoconfig;

import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author HuangXin
 * @since 2019/12/29 13:57
 */
@ConfigurationProperties(
        prefix = "qiniucloud", ignoreInvalidFields = true
)
public class QiNiuCloudProperties {

    public static final String QINIUCLOUD_PREFIX = "qiniucloud";
    private String accessKey; // 设置需要操作的账号的AK和SK
    private String secretKey;
    private String bucketName; // 要上传的空间
    private String domain;   //外链默认域名
    private String style;  //自定义图片样式
    private final Configuration configuration = new Configuration(Zone.huanan());
    private final UploadManager uploadManager = new UploadManager(configuration);

    public QiNiuCloudProperties() {
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public UploadManager getUploadManager() {
        return uploadManager;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String toString() {
        return "QiNiuCloudProperties{" +
                "accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", domain='" + domain + '\'' +
                ", style='" + style + '\'' +
                ", configuration=" + configuration +
                ", uploadManager=" + uploadManager +
                '}';
    }
}
