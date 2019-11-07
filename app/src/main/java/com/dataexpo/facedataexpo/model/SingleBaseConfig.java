package com.dataexpo.facedataexpo.model;

public class SingleBaseConfig {
    private SingleBaseConfig() {

    }

    private static class HolderClass {
        private static final BaseConfig instance = new BaseConfig();
    }

    public static BaseConfig getBaseConfig() {
        return SingleBaseConfig.HolderClass.instance;
    }
}
