package org.easyblog.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class FileUploadUtils {

    /**用户默认头像路径**/
    public static final String DEFAULT_AVATAR= "/static/images/header.jpg";

    public static final String DEFAULT_CATEGORY="/static/images/default/category";

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
        return DEFAULT_CATEGORY+"/"+nextInt+".jpg";
    }

  /*  public static void main(String[] args) {
        for(int i=0;i<1000;i++)
        System.out.println(defaultCategoryImage());
    }*/

}
