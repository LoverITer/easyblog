package org.easyblog.bean;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private static final long serialVersionUID = 9108908247105270923L;
    private Integer  userId;
    private String userNickname;
    private String userPassword;
    private String userName;
    private String userGender;
    private Date userBirthday;
    private String userQQ;
    private String userPhone;
    private String userMail;
    private String userAddress;
    private Integer userScore;
    private Integer userRank;
    private String userHeaderImgUrl;
    private String userDescription;
    private Date userRegisterTime;
    private String userRegisterIp;
    private String userLastLoginIp;
    private String userLastUpdateTime;
    private Integer userLock;
    private Integer userFreeze;
    private Integer userPower;

    public User() {
    }


    public User(String userNickname, String userPassword, String userName, String userGender, Date userBirthday, String userQQ, String userPhone, String userMail, String userAddress, int userScore, int userRank, String userHeaderImgUrl, String userDescription, String userRegisterIp, String userLastLoginIp, String userLastUpdateTime, Integer userLock, Integer userFreeze, Integer userPower) {
        this.userNickname = userNickname;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userGender = userGender;
        this.userBirthday = userBirthday;
        this.userQQ = userQQ;
        this.userPhone = userPhone;
        this.userMail = userMail;
        this.userAddress = userAddress;
        this.userScore = userScore;
        this.userRank = userRank;
        this.userHeaderImgUrl = userHeaderImgUrl;
        this.userDescription = userDescription;
        this.userRegisterIp = userRegisterIp;
        this.userLastLoginIp = userLastLoginIp;
        this.userLastUpdateTime = userLastUpdateTime;
        this.userLock = userLock;
        this.userFreeze = userFreeze;
        this.userPower = userPower;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public String getUserQQ() {
        return userQQ;
    }

    public void setUserQQ(String userQQ) {
        this.userQQ = userQQ;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
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

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public int getUserRank() {
        return userRank;
    }

    public void setUserRank(int userRank) {
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

    public String getUserLastLoginIp() {
        return userLastLoginIp;
    }

    public void setUserLastLoginIp(String userLastLoginIp) {
        this.userLastLoginIp = userLastLoginIp;
    }

    public String getUserLastUpdateTime() {
        return userLastUpdateTime;
    }

    public void setUserLastUpdateTime(String userLastUpdateTime) {
        this.userLastUpdateTime = userLastUpdateTime;
    }

    public int getUserLock() {
        return userLock;
    }

    public void setUserLock(int userLock) {
        this.userLock = userLock;
    }

    public int getUserFreeze() {
        return userFreeze;
    }

    public void setUserFreeze(int userFreeze) {
        this.userFreeze = userFreeze;
    }

    public int getUserPower() {
        return userPower;
    }

    public void setUserPower(int userPower) {
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
                ", uerBirthday=" + userBirthday +
                ", userQQ='" + userQQ + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userMail='" + userMail + '\'' +
                ", userAddress=" + userAddress +
                ", userScore=" + userScore +
                ", userRank=" + userRank +
                ", userHeaderImgUrl='" + userHeaderImgUrl + '\'' +
                ", userDescription='" + userDescription + '\'' +
                ", userRegisterTime=" + userRegisterTime +
                ", userRegisterIp='" + userRegisterIp + '\'' +
                ", userLastLoginIp='" + userLastLoginIp + '\'' +
                ", userLastUpdateTime='" + userLastUpdateTime + '\'' +
                ", userLock=" + userLock +
                ", userFreeze=" + userFreeze +
                ", userPower=" + userPower +
                '}';
    }
}
