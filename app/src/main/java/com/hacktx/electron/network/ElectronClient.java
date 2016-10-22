package com.hacktx.electron.network;

import com.hacktx.electron.BuildConfig;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;

public class ElectronClient {

    private ElectronService electronService;

    private static ElectronClient instance = null;

    public static ElectronClient getInstance() {
        if(instance == null) {
            instance = new ElectronClient();
        }
        return instance;
    }

    protected ElectronClient() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.SERVER_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("HEY"))
                .build();

        electronService = restAdapter.create(ElectronService.class);
    }

    public ElectronService getApiService() {
        return electronService;
    }
}
