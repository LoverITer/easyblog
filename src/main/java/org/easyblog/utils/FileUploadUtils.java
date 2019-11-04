package org.easyblog.utils;

import org.easyblog.config.BASE64DecodeMultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.Random;

public class FileUploadUtils {

    /**用户默认头像路径**/
    public static final String DEFAULT_AVATAR= "/static/images/header.jpg";

    public static final String DEFAULT_CATEGORY="/static/images/";

    private static final Logger  log= LoggerFactory.getLogger(FileUploadUtils.class);

    /**
     * 获得用户默认的头像
     * @return 图片的默认地址
     */
    public static String defaultAvatar(){
       return DEFAULT_AVATAR;
    }

    /***
     * 获得默认的分类头像
     * @return
     */
    public static String defaultCategoryImage(){
        Random random = new Random();
        /**随机产生一个[0,21)的数**/
        int nextInt = random.nextInt(21)+1;
        return DEFAULT_CATEGORY+nextInt+".jpg";
    }

    /**
     * base64格式的图片转为MultipartFile
     * @param base64
     * @return
     */
    public static MultipartFile base64ToMultipart(String base64) {
        try {
            String[] baseStrs = base64.split(",");

            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = new byte[0];
            b = decoder.decodeBuffer(baseStrs[1]);

            for(int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }

            return new BASE64DecodeMultipartFile(b, baseStrs[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
