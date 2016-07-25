package com.ammy.ccleanerdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.ammy.ccleanerdemo.pojo.App;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity {

    private static final String TAG = "appsManager";

    long packageSize = 0, size = 0;
    AppDetails cAppDetails;
    public ArrayList<AppDetails.PackageInfoStruct> res;
    public static final int FETCH_PACKAGE_SIZE_COMPLETED = 100;
    public static final int ALL_PACAGE_SIZE_COMPLETED = 200;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        showProgress("Calculating Cache Size..!!!");
        /**
         * You can also use async task
         * */
        new Thread(new Runnable() {

            @Override
            public void run() {
                getpackageSize();
            }
        }).start();

//        RecyclerView list = (RecyclerView) findViewById(R.id.rv_apps);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        list.setLayoutManager(mLayoutManager);
//        list.setItemAnimator(new DefaultItemAnimator());
//
//        AppsAdapter adapter = new AppsAdapter(getAppsList());
//
//        list.setAdapter(adapter);

    }



    private void getpackageSize() {
        cAppDetails = new AppDetails(this);
        res = cAppDetails.getPackages();
        if (res == null)
            return;
        for (int m = 0; m < res.size(); m++) {
            PackageManager pm = getPackageManager();
            Method getPackageSizeInfo;
            try {
                getPackageSizeInfo = pm.getClass().getMethod(
                        "getPackageSizeInfo", String.class,
                        IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, res.get(m).pname,
                        new cachePackState());
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        handle.sendEmptyMessage(ALL_PACAGE_SIZE_COMPLETED);
        Log.v("Total Cache Size", " " + packageSize);

    }

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FETCH_PACKAGE_SIZE_COMPLETED:
                    if (packageSize > 0)
                        size = (packageSize / 1024000);
                    Log.e("Cahce size","Cache Size : " + size + " MB");
//                    lbl_cache_size.setText("Cache Size : " + size + " MB");
                    break;
                case ALL_PACAGE_SIZE_COMPLETED:
                    if (null != pd)
                        if (pd.isShowing())
                            pd.dismiss();

                    break;
                default:
                    break;
            }

        }

    };

    public class cachePackState extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            Log.d("Package Size", pStats.packageName + "");
            Log.i("Cache Size",pStats.packageName+"  " +pStats.cacheSize + "");
            Log.w("Data Size", pStats.dataSize + "");
            packageSize = packageSize + pStats.cacheSize;
            Log.v("Total Cache Size", " " + pStats.cacheSize);
            handle.sendEmptyMessage(FETCH_PACKAGE_SIZE_COMPLETED);
        }

    }



    private void showProgress(String message) {
        pd = new ProgressDialog(this);
        pd.setIcon(R.mipmap.ic_launcher);
        pd.setTitle("Please Wait...");
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.show();

    }









    public List<App> getAppsList() {

        final PackageManager pm = getPackageManager();
        List<PackageInfo> allApps = pm.getInstalledPackages(PackageManager.GET_META_DATA);

        ArrayList<App> selectedApps = new ArrayList<>();

        for (int i = 0; i < allApps.size(); i++) {
            if (allApps.get(i).versionName != null && ((allApps.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1)) {

                PackageInfo info = allApps.get(i);
                App app = new App();
                app.setEnable(info.applicationInfo.enabled);
                app.setIcon(info.applicationInfo.loadIcon(pm));
                app.setName(info.applicationInfo.loadLabel(pm).toString());
                app.setPackageName(info.applicationInfo.packageName);
                app.setVersionName(info.versionName);

                File file = new File(info.applicationInfo.publicSourceDir);
                long size = file.length();

                app.setApkSize(Utils.byteToReadable(size));

//                Method getPackageSizeInfo = pm.getClass().getMethod(
//                        "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
//
//                getPackageSizeInfo.invoke(pm, "com.android.mms",
//                        new IPackageStatsObserver.Stub() {
//
//                            @Override
//                            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
//                                    throws RemoteException {
//
//                                Log.i(TAG, "codeSize: " + pStats.codeSize);
//                            }
//                        });

                selectedApps.add(app);
            }

        }

        return selectedApps;
    }


//    private class cachePackState extends IPackageStatsObserver.Stub {
//
//        @Override
//        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
//                throws RemoteException {
//            Log.d("Package Size", pStats.packageName + "");
//            Log.i("Cache Size", pStats.cacheSize + "");
//            Log.w("Data Size", pStats.dataSize + "");
//            packageSize = packageSize + pStats.cacheSize;
//            Log.v("Total Cache Size", " " + packageSize);
//            handle.sendEmptyMessage(FETCH_PACKAGE_SIZE_COMPLETED);
//        }
//
//    }

}
