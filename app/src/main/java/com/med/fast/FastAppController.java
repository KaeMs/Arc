package com.med.fast;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Kevin on 4/28/2017. FM
 */

public class FastAppController extends Application {
    public static Realm realm;
    private static boolean activityVisible;
    public static int screenWidth;

    @Override
    public void onCreate() {
        super.onCreate();
        realm = buildDatabase();
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    public Realm buildDatabase(){
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("FM.realm")
                .deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        return Realm.getInstance(realmConfiguration);
    }
}
