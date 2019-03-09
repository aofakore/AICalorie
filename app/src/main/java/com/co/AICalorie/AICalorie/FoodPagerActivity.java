package com.co.AICalorie.AICalorie;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class FoodPagerActivity extends AppCompatActivity {

    private static final String EXTRA_FOOD_ID =
            "com.co.AICalorie.AICalorie.card_id";

    private ViewPager mViewPager;
    private List<Food> mFoods;
    private Food mFood;
    private Day mCurrentDay;

    public static Intent newIntent(Context packageContext, UUID FoodId) {
        Intent intent = new Intent(packageContext, FoodPagerActivity.class);
        intent.putExtra(EXTRA_FOOD_ID, FoodId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_pager);

        UUID foodId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_FOOD_ID);

        mViewPager = (ViewPager) findViewById(R.id.food_view_pager);

        //mCurrentDay = DayLab.get(this).getCurrentDay();
        //mFoods = mCurrentDay.getFoods();
        mFood = FoodLab.get(this).getFood(foodId);
        this.setTitle(mFood.getTitle());

        mFoods = FoodLab.get(this).getFoods(mFood.getDAY_uuid());

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Food food = mFoods.get(position);
                return FoodFragment.newInstance(food.getId());
            }

            @Override
            public int getCount() {
                return mFoods.size();
            }
        });

        for (int i = 0; i < mFoods.size(); i++) {
            if (mFoods.get(i).getId().equals(foodId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
