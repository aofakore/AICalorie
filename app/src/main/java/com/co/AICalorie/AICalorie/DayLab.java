package com.co.AICalorie.AICalorie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.co.AICalorie.AICalorie.database.DayCursorWrapper;
import com.co.AICalorie.AICalorie.database.AICalorieBaseHelper;
import com.co.AICalorie.AICalorie.database.AICalorieDbSchema.DayTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DayLab {

    private static DayLab sDayLab;

    //private List<Day> mDays;

    private Day mCurrentDay;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DayLab get(Context context) {
        if (sDayLab == null) {
            sDayLab = new DayLab(context);
        }

        return sDayLab;
    }

    private DayLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new AICalorieBaseHelper(mContext).getWritableDatabase();

    }

    public void addDay(Day d) {
        ContentValues values = getContentValues(d);
        mDatabase.insert(DayTable.NAME, null, values);
    }

    public void deleteDay(Day d) {
        String selection = DayTable.Cols.UUID + " =?";
        String[] selectionArgs = new String[] { d.getId().toString() };

        mDatabase.delete(DayTable.NAME,
                selection,
                selectionArgs);
    }

    public List<Day> getDays() {
        //return mDays;
        //return new ArrayList<>();
        List<Day> days = new ArrayList<>();

        DayCursorWrapper cursor = queryDays(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                days.add(cursor.getDay());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return days;
    }

    public Day getDay(UUID id) {

        DayCursorWrapper cursor = queryDays(
                DayTable.Cols.UUID + " = ?",
                new String[] {id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getDay();
        } finally {
            cursor.close();
        }

    }

    public Day getCurrentDay() {
        if(mCurrentDay == null){
            return null;
        }else{
            return mCurrentDay;
        }
    }

    public void updateDay(Day day) {
        String uuidString = day.getId().toString();
        ContentValues values = getContentValues(day);

        mDatabase.update(DayTable.NAME, values,
                DayTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    //private Cursor queryDays(String whereClause, String[] whereArgs) {
    private DayCursorWrapper queryDays(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DayTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        //return cursor;
        return new DayCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Day day) {
        ContentValues values = new ContentValues();
        values.put(DayTable.Cols.UUID, day.getId().toString());
        values.put(DayTable.Cols.TITLE, day.getTitle());
        return values;
    }
}
