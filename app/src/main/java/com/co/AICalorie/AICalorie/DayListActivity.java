package com.co.AICalorie.AICalorie;

import android.support.v4.app.Fragment;

public class DayListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DayListFragment();
    }
}
