package top.easyblog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.misc.BASE64Encoder;
import top.easyblog.bean.Category;
import top.easyblog.bean.User;
import top.easyblog.common.pagehelper.PageParam;
import top.easyblog.common.pagehelper.PageSize;
import top.easyblog.common.util.DefaultImageDispatcherUtils;
import top.easyblog.common.util.UserUtils;
import top.easyblog.config.autoconfig.qiniu.QiNiuCloudService;
import top.easyblog.config.web.AjaxResult;
import top.easyblog.service.impl.ArticleServiceImpl;
import top.easyblog.service.impl.CategoryServiceImpl;

import java.util.Objects;


/**
 * 用户后台文章分类管理
 *
 * @visitor huangxin
 */
@Controller
@RequestMapping(value = "/manage/category")
public class CategoryAdminController {

    private static Logger log = LoggerFactory.getLogger(CategoryAdminController.class);
    private final String PREFIX = "/admin/type_manage/";
    private final String LOGIN_PAGE = "redirect:/user/loginPage";
    private final CategoryServiceImpl categoryService;
    private final QiNiuCloudService qiNiuCloudService;
    private final ArticleServiceImpl articleService;


    public CategoryAdminController(CategoryServiceImpl categoryService, QiNiuCloudService qiNiuCloudService, ArticleServiceImpl articleService) {
        this.categoryService = categoryService;
        this.qiNiuCloudService = qiNiuCloudService;
        this.articleService = articleService;
    }


    @GetMapping(value = "/list")
    public String categoryPage(@RequestParam(value = "userId") Integer userId,
                               Model model,
                               @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE.getPageSize());
            PageInfo<Category> categoriesPage = categoryService.getUserAllCategoriesPage(user.getUserId(), pageParam);
            model.addAttribute("categoriesPage", categoriesPage);
            putCategoryNumInModel(user, model);
            return PREFIX + "category-manage";
        }
        return LOGIN_PAGE;
    }

    @ResponseBody
    @RequestMapping(value = "/changeDisplay")
    public AjaxResult switchCategoryDisplay(@RequestParam(value = "categoryId") int categoryId,
                                            @RequestParam(value = "displayStatus") String displayStatus,
                                            @RequestParam(value = "userId") Integer userId) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMessage("请登录后重试！");
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.isNull(user)) {
            ajaxResult.setMessage("请先登录后再操作");
            return ajaxResult;
        }
        try {
            Category category = new Category();
            category.setCategoryId(categoryId);
            category.setDisplay(displayStatus);
            categoryService.updateByPKSelective(category);
            ajaxResult.setSuccess(true);
            ajaxResult.setMessage("Ok");
            return ajaxResult;
        } catch (Exception e) {
            ajaxResult.setMessage("服务异常，请重试！");
            return ajaxResult;
        }
    }

    @GetMapping(value = "/delete")
    public String deleteCategory(@RequestParam(value = "categoryId") int categoryId, @RequestParam(value = "userId") Integer userId) {
        if (categoryId > 0) {
            Category category = new Category();
            category.setCategoryId(categoryId);
            category.setDisplay("3");
            categoryService.updateByPKSelective(category);
            return "redirect:/manage/category/list?userId=" + userId;
        }
        return "/error/404";
    }


    @GetMapping(value = "/restore")
    public String restoreCategory(@RequestParam(value = "userId") Integer userId, @RequestParam int categoryId) {
        if (categoryId > 0) {
            final Category category = new Category();
            category.setCategoryId(categoryId);
            category.setDisplay("1");
            categoryService.updateByPKSelective(category);
            return "redirect:/manage/category/dash?userId=" + userId;
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/deleteComplete")
    public String deleteComplete(@RequestParam(value = "userId") Integer userId,
                                 @RequestParam int categoryId,
                                 @RequestParam String imageUrl) {
        if (categoryId > 0) {
            final Category category = new Category();
            category.setCategoryId(categoryId);
            if (!imageUrl.contains("static")) {
                try {
                    qiNiuCloudService.delete(imageUrl);
                } catch (Exception e) {
                    return "redirect:error/error";
                }
            }
            categoryService.deleteCategoryByCondition(category);
            return "redirect:/manage/category/dash?userId=" + userId;
        }
        return LOGIN_PAGE;
    }


    /**
     * 从分类管理首页删除的分类进入到垃圾箱中，垃圾箱页面
     *
     * @param userId
     * @param model
     * @return
     */
    @GetMapping(value = "/dash")
    public String deleteCategory2DashBoxPage(Model model,
                                             @RequestParam(value = "userId") Integer userId,
                                             @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            PageParam pageParam = new PageParam(pageNo, PageSize.MIN_PAGE_SIZE.getPageSize());
            PageInfo<Category> categoriesPage = categoryService.getUserAllDeletedCategoryPage(user.getUserId(), pageParam);
            model.addAttribute("categoriesPage", categoriesPage);
            putCategoryNumInModel(user, model);
            return PREFIX + "category-dash";
        }
        return LOGIN_PAGE;
    }

    /**
     * 统计分类的数量
     *
     * @param displayStatus 分类的状态 0 不在前台显示  1 在前台显示  2 放在垃圾桶中（不会显示）
     * @return
     */
    private int getCategoryNum(User user, String displayStatus) {
        if (Objects.nonNull(user)) {
            Category category = new Category();
            category.setCategoryUser(user.getUserId());
            category.setDisplay(displayStatus);
            return categoryService.countSelective(category);
        }
        return 0;
    }

    private void putCategoryNumInModel(User user, Model model) {
        int var0 = getCategoryNum(user, "0");
        int var1 = getCategoryNum(user, "1");
        int var2 = getCategoryNum(user, "2");
        model.addAttribute("visibleCategoryNum", var0 + var1);
        model.addAttribute("deletedCategoryNum", var2);
    }

    @GetMapping(value = "/add")
    public String categoryAddPage(@RequestParam(value = "userId") Integer userId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            return PREFIX + "category-add";
        }
        return LOGIN_PAGE;
    }

    @PostMapping(value = "/saveAdd")
    public String saveAdd(@RequestParam(value = "userId") Integer userId,
                          @RequestParam String categoryName,
                          @RequestParam(required = false, defaultValue = "") String categoryDesc,
                          @RequestParam(required = false) MultipartFile categoryImg,
                          RedirectAttributes attributes) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            //用于回显
            attributes.addFlashAttribute("categoryDesc", categoryDesc);
            if (!categoryImg.isEmpty()) {
                try {
                    BASE64Encoder encoder = new BASE64Encoder();
                    // 通过base64来转化图片
                    String data = encoder.encode(categoryImg.getBytes());
                    attributes.addFlashAttribute("categoryImg", "data:image/jpeg;base64," + data);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            if (StringUtil.isEmpty(categoryName)) {
                attributes.addFlashAttribute("error", "专栏名称不能为空");
                return "redirect:/manage/category/add?userId=" + userId;
            }
            //已经存在该分类时，不能重复创建
            Category var0 = categoryService.getCategoryByUserIdAndName(user.getUserId(), categoryName);
            if (Objects.nonNull(var0)) {
                attributes.addFlashAttribute("error", "专栏" + categoryName + "已存在！");
                return "redirect:/manage/category/add?userId=" + userId;
            }
            Category category = new Category(user.getUserId(), categoryName, "", 0, 0, 0, "1", categoryDesc);
            if (categoryImg.isEmpty()) {
                //用户新建分类的时用户没有上传图片，系统随机分配一张
                category.setCategoryImageUrl(DefaultImageDispatcherUtils.defaultCategoryImage());
            } else {
                //上传到七牛云图床，返回图片URL
                try {
                    String imageUrl = qiNiuCloudService.putMultipartImage(categoryImg);
                    category.setCategoryImageUrl(imageUrl);
                } catch (Exception e) {
                    return "/error/error";
                }
            }
            category.setCategoryName(categoryName);
            categoryService.saveCategory(category);
            return "redirect:/manage/category/list?userId=" + userId;
        }
        return LOGIN_PAGE;
    }


    @GetMapping(value = "/edit")
    public String categoryEditor(@RequestParam(value = "userId") Integer userId, @RequestParam int categoryId, Model model) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            model.addAttribute("user", user);
            model.addAttribute("visitor", user);
            final Category category = categoryService.getCategory(categoryId);
            model.addAttribute("category", category);
            return PREFIX + "category-edit";
        }
        return LOGIN_PAGE;
    }


    @PostMapping(value = "/saveEdit/{categoryId}/{userId}")
    public String saveEditor(@PathVariable(value = "userId") Integer userId,
                             @PathVariable("categoryId") int categoryId,
                             @RequestParam String oldCategoryName,
                             @RequestParam String categoryName,
                             @RequestParam String categoryDesc,
                             @RequestParam String oldCategoryImg,
                             @RequestParam MultipartFile categoryImage,
                             RedirectAttributes redirectAttributes) {
        User user = UserUtils.getUserFromRedis(userId);
        if (Objects.nonNull(user)) {
            //判断编辑后的分类名是否为空
            if (StringUtil.isEmpty(categoryName)) {
                redirectAttributes.addFlashAttribute("error", "专栏名称不可为空！");
                return "redirect:/manage/category/edit?categoryId=" + categoryId + "&userId=" + userId;
            }
            //判断编辑后的分类名是否重复
            if (!oldCategoryName.equals(categoryName)) {
                Category var0 = categoryService.getCategoryByUserIdAndName(user.getUserId(), categoryName);
                if (Objects.nonNull(var0)) {
                    //已经存在分类名
                    redirectAttributes.addFlashAttribute("error", "专栏" + categoryName + "已存在！");
                    return "redirect:/manage/category/edit?categoryId=" + categoryId + "&userId=" + userId;
                } else {
                    //没有改分类名
                    int res = articleService.updateArticlesByCategoryName(categoryName, oldCategoryName, user.getUserId());
                    if (res <= 0) {
                        redirectAttributes.addFlashAttribute("error", "发生未知异常！");
                        return "redirect:/manage/category/edit?categoryId=" + categoryId + "&userId=" + userId;
                    }
                }
            }
            try {
                Category category = new Category();
                category.setCategoryId(categoryId);
                category.setCategoryName(categoryName);
                category.setCategoryDescription(categoryDesc);
                if (!categoryImage.isEmpty()) {
                    try {
                        if (!oldCategoryImg.contains("static")) {
                            //删除在七牛云上的图片
                            qiNiuCloudService.delete(oldCategoryImg);
                        }
                        category.setCategoryImageUrl(qiNiuCloudService.putMultipartImage(categoryImage));
                    } catch (Exception e) {
                        return "/error/error";
                    }
                }
                int re = categoryService.updateByPKSelective(category);
                if (re > 0) {
                    return "redirect:/manage/category/list?userId=" + userId;
                } else {
                    return "redirect:/error/404";
                }
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("error", "抱歉！，服务异常，请重试！");
                return "redirect:/manage/category/edit?userId=" + userId;
            }
        }
        return LOGIN_PAGE;
    }

}
