package top.easyblog.entity.po;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangxin
 */
public class UserComment implements Serializable {

    private static final long serialVersionUID = 6652860225449766128L;

    @Id
    private Integer commentId;
    private Integer commentSend;
    private Integer commentReceived;
    private Long articleId;
    private Date commentTime;
    private Integer likeNum;
    private Integer pid;
    private Integer level;
    private String commentContent;

    /*****下面非数据库字段******/
    private String sendUserNickname;   //发送评论的用户的昵称
    private String receivedUserNickname;   //接收到评论的用户的昵称
    private String sendUserNicknameHeadImgUrl;   //发送评论的用户的头像URL
    private List<UserComment> childComment;    //存放所有的子评论
    private Map<String,String> info=new HashMap<>();

    public UserComment() {
    }

    public UserComment( Integer commentSend, Integer commentReceived, Long articleId, Date commentTime, Integer likeNum, Integer pid, Integer level, String commentContent) {
        this.commentSend = commentSend;
        this.commentReceived = commentReceived;
        this.articleId = articleId;
        this.commentTime = commentTime;
        this.likeNum = likeNum;
        this.pid = pid;
        this.level = level;
        this.commentContent = commentContent;
    }

    public String getSendUserNickname() {
        return sendUserNickname;
    }

    public void setSendUserNickname(String sendUserNickname) {
        this.sendUserNickname = sendUserNickname;
    }

    public String getReceivedUserNickname() {
        return receivedUserNickname;
    }

    public void setReceivedUserNickname(String receivedUserNickname) {
        this.receivedUserNickname = receivedUserNickname;
    }

    public String getSendUserNicknameHeadImgUrl() {
        return sendUserNicknameHeadImgUrl;
    }

    public void setSendUserNicknameHeadImgUrl(String sendUserNicknameHeadImgUrl) {
        this.sendUserNicknameHeadImgUrl = sendUserNicknameHeadImgUrl;
    }

    public List<UserComment> getChildComment() {
        return childComment;
    }

    public void setChildComment(List<UserComment> childComment) {
        this.childComment = childComment;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getCommentSend() {
        return commentSend;
    }

    public void setCommentSend(Integer commentSend) {
        this.commentSend = commentSend;
    }

    public Integer getCommentReceived() {
        return commentReceived;
    }

    public void setCommentReceived(Integer commentReceived) {
        this.commentReceived = commentReceived;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent == null ? null : commentContent.trim();
    }


    @Override
    public String toString() {
        return "UserComment{" +
                "commentId=" + commentId +
                ", commentSend=" + commentSend +
                ", commentReceived=" + commentReceived +
                ", articleId=" + articleId +
                ", commentTime=" + commentTime +
                ", likeNum=" + likeNum +
                ", pid=" + pid +
                ", level=" + level +
                ", commentContent='" + commentContent + '\'' +
                '}';
    }
}