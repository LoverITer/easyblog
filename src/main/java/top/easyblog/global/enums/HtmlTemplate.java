package top.easyblog.global.enums;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/08/22 21:10
 */
public enum HtmlTemplate {

    TEST("<p th:text='${title}'></p>"),


    INDEX_NEW_ARTICLE_LIST("<li th:if=${newestArticlesPages!=null&&!#lists.isEmpty(newestArticlesPages.getList())} th:each=article:${newestArticlesPages.getList()}>" +
            "                    <h3 class=blogtitle>" +
            "                        <a href=/xd/23.html th:if=${user==null} th:href=@{/article/details/{id}(id=${article.getArticleId()})} target=_blank th:text=${article.getArticleTopic()}>作为一个设计师,如果遭到质疑你是否能恪守自己的原则?</a>" +
            "                        <a href=/xd/23.html th:if=${user!=null} th:href=@{/article/details/{id}(id=${article.getArticleId()},visitorUId=${user.getUserId()})} target=_blank th:text=${article.getArticleTopic()}>作为一个设计师,如果遭到质疑你是否能恪守自己的原则?</a>" +
            "                    </h3>" +
            "                    <span class=blogpic imgscale>" +
            "                     <a href=/xd/23.html th:if=${user!=null} th:href=@{/article/details/{id}(id=${article.getArticleId()},visitorUId=${user.getUserId()})}" +
            "                        th:title=${article.getArticleTopic()}>" +
            "                         <img th:if=${article.getArticleFirstPicture()==null} src=https://uploadbeta.com/api/pictures/random/?key=BingEverydayWallpaperPicture th:alt=${article.getArticleTopic()} alt=作为一个设计师,如果遭到质疑你是否能恪守自己的原则?>" +
            "                         <img th:if=${article.getArticleFirstPicture()!=null} th:src=@{${article.getArticleFirstPicture()}} th:alt=${article.getArticleTopic()} alt=作为一个设计师,如果遭到质疑你是否能恪守自己的原则?>" +
            "                     </a>" +
            "                     <a href=/xd/23.html th:if=${user==null} th:href=@{/article/details/{id}(id=${article.getArticleId()})}" +
            "                           th:title=${article.getArticleTopic()}>" +
            "                         <img th:if=${article.getArticleFirstPicture()==null} src=https://uploadbeta.com/api/pictures/random/?key=BingEverydayWallpaperPicture th:alt=${article.getArticleTopic()} alt=作为一个设计师,如果遭到质疑你是否能恪守自己的原则?>" +
            "                         <img th:if=${article.getArticleFirstPicture()!=null} th:src=@{${article.getArticleFirstPicture()}} th:alt=${article.getArticleTopic()} alt=作为一个设计师,如果遭到质疑你是否能恪守自己的原则?>" +
            "                     </a>" +
            "                    </span>" +
            "                    <p th:text=${#strings.abbreviate(article.getArticleContent(),150)} class=blogtext>" +
            "                    </p>" +
            "                    <p class=bloginfo>" +
            "                        <i class=avatar>" +
            "                            <a href=# th:href=@{/article/index/{userId}(userId=${article.getArticleUser()})} target=_blank>" +
            "                                <img src=# th:src=@{${article.getUserHeaderImageUrl()}} width=30px height=30px>" +
            "                            </a>" +
            "                        </i>" +
            "                        <span></span>" +
            "                        <span th:text=${#dates.format(article.getArticlePublishTime(),'yyyy-MM-dd')}>2018-11-08</span>" +
            "                        <span class=text>" +
            "                          <svg style=vertical-align:-5px;fill: #666 t=1568705845189 class=icon eye viewBox=0 0 1433 1024 version=1.1 xmlns=http://www.w3.org/2000/svg p-id=16883 width=20 height=20><path d=M720.69629629 932.6176963c-213.05768889 0-407.36918519-120.58848889-582.93448888-361.67534074-23.88337036-32.71571111-23.88337036-85.169 0-117.88471112C313.32711111 211.97079259 507.63860741 91.3823037 720.69629629 91.3823037s407.36918519 120.58848889 582.9344889 361.67534074c23.88337036 32.71571111 23.88337036 85.169 0 117.88471112C1128.06548147 812.02920741 933.75398519 932.6176963 720.69629629 932.6176963z m0-721.00740741C542.51734074 211.61028889 377.85727408 309.39691852 222.48017777 512 377.85727408 714.60308148 542.4272148 812.38971111 720.69629629 812.38971111c178.17895556 0 342.83902223-97.78662964 498.21611851-300.38971111C1063.53531852 309.39691852 898.96537777 211.61028889 720.69629629 211.61028889z m0 495.69259259a195.30288148 195.30288148 0 1 1 0-390.60576296 195.30288148 195.30288148 0 0 1 0 390.60576296zM720.69629629 602.12592592a90.12592592 90.12592592 0 1 0 0-180.25185184 90.12592592 90.12592592 0 0 0 0 180.25185184z p-id=16884 fill=></path></svg>" +
            "                         <span style=margin-left: 0 th:text=${article.getArticleClick()}></span>" +
            "                        </span>" +
            "                        <span>【<a href=/xd/ th:href=@{/category/details/{categoryId}/{userId}(categoryId=${article.getCategoryId()},userId=${article.getArticleUser()})} target=_blank th:text=${article.getArticleCategory()}>设计心得</a>】</span>" +
            "                    </p>" +
            "                    <a href=/xd/23.html th:if=${user==null} th:href=@{/article/details/{id}(id=${article.getArticleId()})} class=viewmore>阅读全文</a>" +
            "                    <a href=/xd/23.html th:if=${user!=null} th:href=@{/article/details/{id}(id=${article.getArticleId()},visitorUId=${user.getUserId()})} class=viewmore>阅读全文</a>" +
            "                </li>");


    private final String template;

    HtmlTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
