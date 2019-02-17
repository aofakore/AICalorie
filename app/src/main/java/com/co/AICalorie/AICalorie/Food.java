package com.co.AICalorie.AICalorie;

import java.util.Date;
import java.util.UUID;

public class Food {

    private UUID mId;
    private String mTitle;
    private String mText;
    private boolean mIsShown;
    private UUID mDAY_uuid;
    private Date mDate;

    public Food() {
        this(UUID.randomUUID());
    }

    public Food(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public boolean isShown() {
        return mIsShown;
    }

    public void setShown(boolean show) {
        mIsShown = show;
    }

    public UUID getDAY_uuid() {
        return mDAY_uuid;
    }

    public void setDAY_uuid(UUID DAY_uuid) {
        mDAY_uuid = DAY_uuid;
    }

    public Date getDate(){
        return mDate;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
