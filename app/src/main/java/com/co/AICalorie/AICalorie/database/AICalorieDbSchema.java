package com.co.AICalorie.AICalorie.database;

public class AICalorieDbSchema {

    public static final class FoodTable {
        public static final String NAME = "cards";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String DAY_UUID = "deck_uuid";
            public static final String TITLE = "title";
            public static final String TEXT = "text";
            public static final String SHOWN = "shown";
        }
    }

    public static final class DayTable {
        public static final String NAME = "decks";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
        }
    }
}