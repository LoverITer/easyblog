package top.easyblog.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import top.easyblog.autoconfig.QiNiuCloudService;
import top.easyblog.config.web.Result;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Controller
@RequestMapping(value = "/upload")
public class FileUploadController {

    private final QiNiuCloudService qiNiuCloudService;

    public FileUploadController(QiNiuCloudService qiNiuCloudService) {
        this.qiNiuCloudService = qiNiuCloudService;
    }

    @RequestMapping(value = "/interface1")
    public Result fileUpload(@RequestParam(value = "file") MultipartFile multipartFile, HttpServletRequest request,@RequestParam int categoryId){
        Result result = new Result();
        result.setSuccess(false);
        String fileName = multipartFile.getOriginalFilename();
        fileName = System.currentTimeMillis() + "_" + fileName;
        String path = "E:/fileUpload/" +fileName;
        System.out.println(path);
        File uploads = new File(path);
        if(!uploads.exists()&&!uploads.isDirectory()){
            uploads.mkdirs();
        }
        String url="";
        try {
           if(Objects.nonNull(fileName)) {
               String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase();

               if (".jpg".equals(suffix) || ".png".equals(suffix) || ".gif".equals(suffix) || ".jpeg".equals(suffix) || "bmp".equals(suffix)) {
                   //给上传的文件重新命名
                   File newFile = new File(path, fileName);
                   //保存文件到服务器
                   multipartFile.transferTo(newFile);
                   result.setMsg(path + fileName);
                   //url="http://你自己的域名/项目名/images/"+fileName;//正式项目
                   url="http://localhost:8080/images/"+fileName;//本地运行项目
                   result.setMsg(url);
               } else {
                   result.setMsg("文件上传失败！只支持jpeg, jpg, png, gif, bmp 格式的图片文件");
               }
           }else{
               result.setMsg("服务异常，请重试！");
           }
        } catch (IOException e) {
            result.setMsg("服务异常，请重试！");
            e.printStackTrace();
            return result;
        }
        return result;
    }

    @ResponseBody
    @PostMapping(value = "/interface2")
    public Result upload2QiNiuCloud(@RequestParam MultipartFile multipartFile){
        Result result = new Result();
        result.setSuccess(false);
        try {
            String imageUrl = qiNiuCloudService.putMultipartImage(multipartFile);
            //上传成功后把图片的URL带回页面
            result.setMsg(imageUrl);
            result.setSuccess(true);
        }catch (Exception e){
            result.setMsg("抱歉！服务异常，请重试！");
            return result;
        }
        return result;
    }


    /**
     * 把图片上传到七牛云
     * editor.md期望得到一个json格式的上传后的返回值，格式是这样的：
     *
     *    {
     *         success : 0 | 1,           // 0 表示上传失败，1 表示上传成功
     *         message : "提示的信息，上传成功或上传失败及错误信息等。",
     *         url     : "图片地址"        // 上传成功时才返回
     *      }
     *
     * @param multipartFile
     * @return 将会返回图片的URL地址
     */
    @ResponseBody
    @PostMapping(value = "/interface3")
    public JSONObject saveArticlesImage(@RequestParam(value = "editormd-image-file") MultipartFile multipartFile){
        JSONObject resultJs=new JSONObject();
        try {
            String imageUrl = qiNiuCloudService.putMultipartImage(multipartFile);
            //上传成功后把图片的URL带回页面
            resultJs.put("success", 1);
            resultJs.put("message", "上传成功");
            resultJs.put("url",imageUrl);
        }catch (Exception e){
            resultJs.put("success", 0);
            resultJs.put("message", "上传失败");
        }
        return resultJs;
    }


}
