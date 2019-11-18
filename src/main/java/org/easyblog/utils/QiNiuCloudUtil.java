package org.easyblog.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


public class QiNiuCloudUtil {

    private Logger log = LoggerFactory.getLogger(QiNiuCloudUtil.class);
    private volatile static QiNiuCloudUtil qiNiuCloudUtil = null;

    // 设置需要操作的账号的AK和SK
    private static final String ACCESS_KEY = "vRxHtqsnLfzK2h2DTGcEfFlLLDDNgqAvoZf0H08D";
    private static final String SECRET_KEY = "5No0lIzPOv2pYDX9wPkFFh_kE99lvpPia0nA0ZPH";
    // 要上传的空间
    private static final String bucketname = "huangxin981230";
    // 密钥
    private static final Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //外链默认域名
    private static final String DOMAIN = "q0hiemlhp.bkt.clouddn.com";
    //自定义图片样式
    private static final String style = "imageView2/1/w/200/h/200/format/jpg/q/100|imageslim";

    //构造一个带指定Zone对象的配置类
    private Configuration configuration = new Configuration(Zone.huanan());
    private UploadManager uploadManager = new UploadManager(configuration);

    private QiNiuCloudUtil() {
    }


    public static QiNiuCloudUtil getInstance() {
        if (null == qiNiuCloudUtil) {
            synchronized (QiNiuCloudUtil.class) {
                if (null == qiNiuCloudUtil) {
                    qiNiuCloudUtil = new QiNiuCloudUtil();
                }
            }
        }
        return qiNiuCloudUtil;
    }

    private String getUpToken() {
        return auth.uploadToken(bucketname, null, 3600, new StringMap().put("insertOnly", 1));
    }

    // 普通上传
    public String upload(String filePath, String fileName) throws IOException {
        try {
            // 调用put方法上传
            String token = auth.uploadToken(bucketname);
            Response res = uploadManager.put(filePath, fileName, token);
            log.info("返回信息：{}", res.bodyString());
            if (res.isOK()) {
                Ret ret = res.jsonToObject(Ret.class);
                //如果不需要对图片进行样式处理，则使用以下方式即可
                return "http://" + DOMAIN + "/" + ret.key;
                //return "http://" + DOMAIN + "/" + ret.key + "?" + style;
            }
        } catch (QiniuException e) {
            Response r = e.response;
            log.error("异常信息：{}", r.toString());
            try {
                log.error("异常响应信息：{}", r.bodyString());
            } catch (QiniuException e1) {
                // ignore
            }
        }
        return null;
    }


    /**
     * 以MultipartFile的形式上传
     *
     * @param multipartFile 前端提交过来的文件数据
     * @return 返回图片直接可访问的URL
     */
    public String putMultipartImage(MultipartFile multipartFile) {
        //解析文件后缀名
        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
        String key = System.currentTimeMillis() + UUID.randomUUID().toString() + suffix;
        try {
            byte[] uploadBytes = multipartFile.getBytes();
            putBase64Image(uploadBytes, key);
        } catch (IOException e) {
            throw new RuntimeException("上传图片失败");
        }
        //如果不需要添加图片样式，使用以下方式
        return "http://" + DOMAIN + "/" + key;
        //return "http://" + DOMAIN + "/" + key + "?" + style;
    }

    /**
     * 以字节数组(byte[])的形式上传
     * @param bytes  字节数组，图片数据
     * @param key   图片的名字 如果为null，将会把图片的hashcode作为名字
     * @return 返回图片直接可访问的URL
     */
    public String putBase64Image(byte[] bytes, String key) {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String upToken = getUpToken();
        DefaultPutRet putRet=null;
        try {
            Response response = uploadManager.put(bytes, key, upToken);
            //解析上传成功的结果
            putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                throw new RuntimeException("上传图片失败");
            }
        }
        //如果不需要添加图片样式，使用以下方式
        assert putRet != null;
        return null==key?("http://" + DOMAIN + "/" +putRet.key):("http://" + DOMAIN + "/" +key);
        //return "http://" + DOMAIN + "/" + key + "?" + style;
    }


    /**
     * 删除在七牛云上的文件
     *
     * @param imageUrl 文件URL
     */
    public void delete(String imageUrl) {
        String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.length());
        // 实例化一个BucketManager对象
        BucketManager bucketManager = new BucketManager(auth, configuration);
        try {
            // 调用delete方法移动文件
            bucketManager.delete(bucketname, key);
        } catch (QiniuException e) {
            // 捕获异常信息
            Response r = e.response;
            log.error(r.toString());
        }
    }

    static class Ret {
        public long fsize;
        public String key;
        public String hash;
        public int width;
        public int height;
    }

    public static void main(String[] args) {
        QiNiuCloudUtil.getInstance().delete("http://q0hiemlhp.bkt.clouddn.com/Fkm739RtjMdHh4aZUs0ZG2V0wwvY");
    }
}
