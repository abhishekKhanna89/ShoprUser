package com.shoppr.shoper.activity;

import android.app.Application;

/**
 * Created by Rahul Hooda on 14/7/17.
 */
public class BaseApplicationpay extends Application {

    AppEnvironment appEnvironment;

    @Override
    public void onCreate() {
        super.onCreate();
        appEnvironment = AppEnvironment.SANDBOX;
    }
    AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }
}
