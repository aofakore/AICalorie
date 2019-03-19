package com.co.AICalorie.AICalorie;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Day {

    private UUID mId;
    private String mTitle;
    private List<Food> mFoods;

    public Day() {

        this(UUID.randomUUID());
    }

    public Day(UUID id) {
        mId = id;
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

    public String getDate() {
        String pattern = "MMMM d, yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        String dayListDate = df.format(today);

        return dayListDate;
    }
}
