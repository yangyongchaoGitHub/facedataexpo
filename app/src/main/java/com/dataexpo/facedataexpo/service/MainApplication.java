package com.dataexpo.facedataexpo.service;

import android.app.Application;

public class MainApplication extends Application {
    private static MainApplication application;
    private BgService service;

    public BgService getService() {
        return service;
    }

    public void setService(BgService service) {
        this.service = service;
    }

    public static MainApplication getInstance() {
        return  application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
