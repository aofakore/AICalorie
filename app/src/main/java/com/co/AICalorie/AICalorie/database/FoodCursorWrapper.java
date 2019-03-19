package com.co.AICalorie.AICalorie.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.co.AICalorie.AICalorie.Food;
import com.co.AICalorie.AICalorie.database.AICalorieDbSchema.FoodTable;

import java.util.UUID;

public class FoodCursorWrapper extends CursorWrapper {
    public FoodCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Food getFood() {
        String uuidString = getString(getColumnIndex(FoodTable.Cols.UUID));
        String title = getString(getColumnIndex(FoodTable.Cols.TITLE));
        String text = getString(getColumnIndex(FoodTable.Cols.TEXT));
        String day_uuidString = getString(getColumnIndex(FoodTable.Cols.DAY_UUID));
        int isShown = getInt(getColumnIndex(FoodTable.Cols.SHOWN));
        Double calorie = getDouble(getColumnIndex(FoodTable.Cols.CALORIE));

        Food food = new Food(UUID.fromString(uuidString));  //TODO day UUID too
        food.setTitle(title);
        food.setDAY_uuid(UUID.fromString(day_uuidString));
        food.setText(text);
        food.setShown(isShown != 0);
        food.setCalorie(calorie);
        return food;

    }
}

