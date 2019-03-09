package com.co.AICalorie.AICalorie;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class FoodListActivity extends SingleFragmentActivity {

    public static final String EXTRA_DAY_ID =
            "come.co.AICalorie.AICalorie.deck_id";

    public static Intent newIntent(Context packageContent, UUID dayId) {
        Intent intent = new Intent(packageContent, FoodListActivity.class);
        intent.putExtra(EXTRA_DAY_ID, dayId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new FoodListFragment();
    }
}
