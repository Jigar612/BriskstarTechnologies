package com.example.jigarpractical.baseclass;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jigarpractical.R;
import com.example.jigarpractical.utility.AppUtil;

import static com.example.jigarpractical.utility.AppUtil.isValidString;


public class BaseActivity extends AppCompatActivity {

    private final static String TAG = BaseActivity.class.getSimpleName();
    private final Context mContext = this;
    public AppUtil appUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For FileURIExposedException Handling
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        appUtil = new AppUtil();

        // To Prevent NetworkOnMainThread Exception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


    }

    protected void fullScreen() {//Hide StatusBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    public void showInternetToast() {
        Toast.makeText(mContext, getString(R.string.validation_please_check_your_internet_connection), Toast.LENGTH_SHORT).show();
    }

    public void showErrorToast() {
        Toast.makeText(mContext, getString(R.string.validation_something_went_wrong), Toast.LENGTH_SHORT).show();
    }

    public void showToast(String message, boolean shortDuration) {
        if (isValidString(message))
            if (shortDuration)
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
