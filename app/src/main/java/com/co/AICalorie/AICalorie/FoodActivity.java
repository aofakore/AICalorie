package com.co.AICalorie.AICalorie;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class FoodActivity extends SingleFragmentActivity {

    private static final String EXTRA_FOOD_ID =
            "com.co.AICalorie.AICalorie.card_id";

    public static Intent newIntent(Context packageContext, UUID foodId) {
        Intent intent = new Intent(packageContext, FoodActivity.class);
        intent.putExtra(EXTRA_FOOD_ID, foodId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {

        UUID foodId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_FOOD_ID);
        return FoodFragment.newInstance(foodId);
    }
}
