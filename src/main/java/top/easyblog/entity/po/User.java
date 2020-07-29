package top.easyblog.entity.po;

import top.easyblog.common.enums.ArticleType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Huangxin
 */
public class User implements Serializable {

    private static final long serialVersionUID = 9108908247105270923L;
    private Integer userId;
    private String userNickname;
    private String userPassword;
    private String userName;
    private String userGender;
    private Date userBirthday;
    private String userPhone;
    private String userMail;
    private String userAddress;
    private Integer userScore;
    private Integer userRank;
    private String userHeaderImgUrl;
    private String userDescription;
    private Date userRegisterTime;
    private String userRegisterIp;
    private String userLastUpdateTime;
    private Integer userLock;
    private Integer userFreeze;
    private Integer userPower;
    private Integer userLevel;
    private Integer userVisit;
    private String userJobPosition;
    private String userPrefession;
    private String userHobby;
    private String userTech;


    public User() {
    }


    public User(String userNickname, String userPassword, String userName, String userGender, Date userBirthday, String userPhone, String userMail, String userAddress, int userScore, int userRank, String userHeaderImgUrl, String userDescription, String userRegisterIp, String userLastUpdateTime, Integer userLock, Integer userFreeze, Integer userPower, Integer userLevel, Integer userVisit) {
        this.userNickname = userNickname;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userGender = userGender;
        this.userBirthday = userBirthday;
        this.userPhone = userPhone;
        this.userMail = userMail;
        this.userAddress = userAddress;
        this.userScore = userScore;
        this.userRank = userRank;
        this.userHeaderImgUrl = userHeaderImgUrl;
        this.userDescription = userDescription;
        this.userRegisterIp = userRegisterIp;
        this.userLastUpdateTime = userLastUpdateTime;
        this.userLock = userLock;
        this.userFreeze = userFreeze;
        this.userPower = userPower;
        this.userLevel = userLevel;
        this.userVisit = userVisit;
    }

    public String getUserJobPosition() {
        return userJobPosition;
    }

    public void setUserJobPosition(String userJobPosition) {
        this.userJobPosition = userJobPosition;
    }

    public String getUserPrefession() {
        return userPrefession;
    }

    public void setUserPrefession(String userPrefession) {
        this.userPrefession = userPrefession;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public Integer getUserVisit() {
        return userVisit;
    }

    public void setUserVisit(Integer userVisit) {
        this.userVisit = userVisit;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public Date getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(Date userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserHobby() {
        return userHobby;
    }

    public void setUserHobby(String userHobby) {
        this.userHobby = userHobby;
    }

    public String getUserTech() {
        return userTech;
    }

    public void setUserTech(String userTech) {
        this.userTech = userTech;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Integer getUserScore() {
        return userScore;
    }

    public void setUserScore(String type) {
        if (ArticleType.Original.getArticleType().equals(type)) {
            this.userScore = this.userScore + 5;   //原创文章+5分
        } else if (ArticleType.Translate.getArticleType().equals(type)) {
            this.userScore = this.userScore + 3;   //翻译文章+3分
        } else {
            this.userScore = this.userScore + 1;   //转载的文章+1分
        }
    }

    public void setUserScore(Integer userScore) {
        this.userScore = userScore;
    }

    public Integer getUserRank() {
        return userRank;
    }

    public void setUserRank() {
        if (this.userScore < 256) {
            setUserRank(1);
        } else if (this.userScore < 512) {
            setUserRank(2);
        } else if (this.userScore < 1024) {
            setUserRank(3);
        } else if (this.userScore < 2048) {
            setUserRank(4);
        } else if (this.userScore < 4096) {
            setUserRank(5);
        } else if (this.userScore < 8192) {
            setUserRank(6);
        } else if (this.userScore < 16384) {
            setUserRank(7);
        } else if (this.userScore < 32768) {
            setUserRank(8);
        } else if (this.userScore < 65536) {
            setUserRank(9);
        } else if (this.userScore < 131072) {
            setUserRank(10);
        } else if (this.userScore < 262144) {
            setUserRank(11);
        } else if (this.userScore < 524288) {
            setUserRank(12);
        } else if (this.userScore < 1048576) {
            setUserRank(13);
        } else if (this.userScore < 2097152) {
            setUserRank(14);
        } else if (this.userScore < 4194304) {
            setUserRank(15);
        } else if (this.userScore < 8388608) {
            setUserRank(16);
        } else if (this.userScore < 16777216) {
            setUserRank(17);
        } else {
            setUserRank(this.userRank);
        }

    }

    public void setUserRank(Integer userRank) {
        this.userRank = userRank;
    }

    public String getUserHeaderImgUrl() {
        return userHeaderImgUrl;
    }

    public void setUserHeaderImgUrl(String userHeaderImgUrl) {
        this.userHeaderImgUrl = userHeaderImgUrl;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public Date getUserRegisterTime() {
        return userRegisterTime;
    }

    public void setUserRegisterTime(Date userRegisterTime) {
        this.userRegisterTime = userRegisterTime;
    }

    public String getUserRegisterIp() {
        return userRegisterIp;
    }

    public void setUserRegisterIp(String userRegisterIp) {
        this.userRegisterIp = userRegisterIp;
    }

    public String getUserLastUpdateTime() {
        return userLastUpdateTime;
    }

    public void setUserLastUpdateTime(String userLastUpdateTime) {
        this.userLastUpdateTime = userLastUpdateTime;
    }

    public Integer getUserLock() {
        return userLock;
    }

    public void setUserLock(Integer userLock) {
        this.userLock = userLock;
    }

    public Integer getUserFreeze() {
        return userFreeze;
    }

    public void setUserFreeze(Integer userFreeze) {
        this.userFreeze = userFreeze;
    }

    public Integer getUserPower() {
        return userPower;
    }

    public void setUserPower(Integer userPower) {
        this.userPower = userPower;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userNickname='" + userNickname + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userName='" + userName + '\'' +
                ", userGender='" + userGender + '\'' +
                ", userBirthday=" + userBirthday +
                ", userPhone='" + userPhone + '\'' +
                ", userMail='" + userMail + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", userScore=" + userScore +
                ", userRank=" + userRank +
                ", userHeaderImgUrl='" + userHeaderImgUrl + '\'' +
                ", userDescription='" + userDescription + '\'' +
                ", userRegisterTime=" + userRegisterTime +
                ", userRegisterIp='" + userRegisterIp + '\'' +
                ", userLastUpdateTime='" + userLastUpdateTime + '\'' +
                ", userLock=" + userLock +
                ", userFreeze=" + userFreeze +
                ", userPower=" + userPower +
                ", userLevel=" + userLevel +
                ", userVisit=" + userVisit +
                ", userJobPosition='" + userJobPosition + '\'' +
                ", userPrefession='" + userPrefession + '\'' +
                '}';
    }
}
