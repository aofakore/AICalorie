package com.co.AICalorie.AICalorie;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.co.AICalorie.AICalorie.common.helpers.CameraPermissionHelper;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

public class DayListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DayListFragment();
    }

}
