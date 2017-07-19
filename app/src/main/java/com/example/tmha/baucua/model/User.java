package com.example.tmha.baucua.model;

/**
 * Created by tmha on 7/19/2017.
 */

public class User {
    private String mId;
    private String mName;
    private String mProfilePhoto;
    private String mEmail;
    private String mTotalMoney;
    private String mGender;
    private String mBirthday;

    public User() {
    }

    public User(String mId, String mName,
                String mProfilePhoto, String mEmail, String mTotalMoney) {
        this.mId = mId;
        this.mName = mName;
        this.mProfilePhoto = mProfilePhoto;
        this.mEmail = mEmail;
        this.mTotalMoney = mTotalMoney;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmProfilePhoto() {
        return mProfilePhoto;
    }

    public void setmProfilePhoto(String mProfilePhoto) {
        this.mProfilePhoto = mProfilePhoto;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmTotalMoney() {
        return mTotalMoney;
    }

    public void setmTotalMoney(String mTotalMoney) {
        this.mTotalMoney = mTotalMoney;
    }
}
