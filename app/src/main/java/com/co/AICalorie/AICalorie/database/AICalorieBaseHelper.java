package com.co.AICalorie.AICalorie.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.co.AICalorie.AICalorie.database.AICalorieDbSchema.FoodTable;
import com.co.AICalorie.AICalorie.database.AICalorieDbSchema.DayTable;


public class AICalorieBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "AICalorieBase.db";

    // Food table create statement
    private static final String create_table_foods = "create table " + FoodTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            FoodTable.Cols.UUID + ", " +
            FoodTable.Cols.DAY_UUID + ", " +
            FoodTable.Cols.TITLE + ", " +
            FoodTable.Cols.TEXT + ", " +
            FoodTable.Cols.SHOWN + ", " +
            FoodTable.Cols.CALORIE + ")";

    // Day table create statement
    private static final String create_table_days = "create table " + DayTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            DayTable.Cols.UUID + ", " +
            DayTable.Cols.TITLE + ")";

    public AICalorieBaseHelper(Context context) {

        super(context, DATABASE_NAME, null, VERSION);
    }

    public String getDailyCalorie(){

        return "sum";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table_foods);
        db.execSQL(create_table_days);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}

