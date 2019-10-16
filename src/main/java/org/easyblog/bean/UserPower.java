package org.easyblog.bean;

import java.io.Serializable;

public class UserPower implements Serializable {

    private static final long serialVersionUID = -3934031817257705616L;
    private Byte powerId;

    private String powerName;

    public Byte getPowerId() {
        return powerId;
    }

    public void setPowerId(Byte powerId) {
        this.powerId = powerId;
    }

    public String getPowerName() {
        return powerName;
    }

    public void setPowerName(String powerName) {
        this.powerName = powerName == null ? null : powerName.trim();
    }

    @Override
    public String toString() {
        return "UserPower{" +
                "powerId=" + powerId +
                ", powerName='" + powerName + '\'' +
                '}';
    }
}