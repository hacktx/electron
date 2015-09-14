package com.hacktx.electron.network;

import retrofit.RestAdapter;

public class ElectronClient {

    private static final String ELECTRON_BASE_URL = "https://my.hacktx.com/api/";
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
                .setEndpoint(ELECTRON_BASE_URL)
                .build();

        electronService = restAdapter.create(ElectronService.class);
    }

    public ElectronService getApiService() {
        return electronService;
    }
}
