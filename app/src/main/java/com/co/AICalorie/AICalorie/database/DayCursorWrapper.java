package com.co.AICalorie.AICalorie.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.co.AICalorie.AICalorie.Day;
import com.co.AICalorie.AICalorie.database.AICalorieDbSchema.DayTable;

import java.util.UUID;

public class DayCursorWrapper extends CursorWrapper {
    public DayCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Day getDay() {
        String uuidString = getString(getColumnIndex(DayTable.Cols.UUID));
        String title = getString(getColumnIndex(DayTable.Cols.TITLE));

        Day day = new Day(UUID.fromString(uuidString));
        day.setTitle(title);
        return day;
    }
}
