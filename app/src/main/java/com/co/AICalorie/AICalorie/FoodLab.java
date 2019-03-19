package com.co.AICalorie.AICalorie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.co.AICalorie.AICalorie.database.FoodCursorWrapper;
import com.co.AICalorie.AICalorie.database.AICalorieDbSchema.FoodTable;
import com.co.AICalorie.AICalorie.database.AICalorieBaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FoodLab {

    private static FoodLab sFoodLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static FoodLab get(Context context) {
        if (sFoodLab == null) {
            sFoodLab = new FoodLab(context);
        }

        return sFoodLab;
    }

    private FoodLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new AICalorieBaseHelper(mContext).getWritableDatabase();
    }

    public void addFood(Food c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(FoodTable.NAME, null, values);
    }

    public void deleteFood(Food c) {
        String selection = FoodTable.Cols.UUID + " =?";
        String[] selectionArgs = new String[] { c.getId().toString() };

        mDatabase.delete(FoodTable.NAME,
                selection,
                selectionArgs);
    }

    public List<Food> getFoods(UUID dayId) {

        List<Food> foods = new ArrayList<>();

        String selection = FoodTable.Cols.DAY_UUID + " =?";
        String[] selectionArgs = new String[] { dayId.toString() };

        FoodCursorWrapper cursor = queryFoods(selection, selectionArgs);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return foods;
    }

    public Food getFood(UUID id) {
        FoodCursorWrapper cursor = queryFoods(
                FoodTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getFood();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Food food) {
        File filesDir = mContext.getFilesDir();
        return  new File(filesDir, food.getPhotoFilename());
    }

    public void updateFood(Food food) {
        String uuidString = food.getId().toString();
        ContentValues values = getContentValues(food);

        mDatabase.update(FoodTable.NAME, values,
                FoodTable.Cols.UUID + " =?",
                new String[] {uuidString});
    }


    private FoodCursorWrapper queryFoods(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                FoodTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new FoodCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Food food){
        ContentValues values = new ContentValues();
        values.put(FoodTable.Cols.UUID, food.getId().toString());
        values.put(FoodTable.Cols.TITLE, food.getTitle());
        values.put(FoodTable.Cols.TEXT, food.getText());
        values.put(FoodTable.Cols.DAY_UUID, food.getDAY_uuid().toString());
        values.put(FoodTable.Cols.SHOWN, food.isShown() ? 1 : 0 );
        values.put(FoodTable.Cols.CALORIE, food.getCalorie());
        return values;
    }
}
