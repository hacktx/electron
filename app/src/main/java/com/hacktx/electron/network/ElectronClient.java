package com.hacktx.electron.network;

import com.hacktx.electron.BuildConfig;

import retrofit.RestAdapter;

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
                .setEndpoint(BuildConfig.NUCLEUS_URL)
                .build();

        electronService = restAdapter.create(ElectronService.class);
    }

    public ElectronService getApiService() {
        return electronService;
    }
}
