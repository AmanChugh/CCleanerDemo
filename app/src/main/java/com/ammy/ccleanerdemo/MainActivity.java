package com.ammy.ccleanerdemo;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String SD_CARD = "sdCard";
    public static final String EXTERNAL_SD_CARD = "externalSdCard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String externalStor = Environment.getExternalStorageDirectory().getAbsolutePath();

        String internal =  getFilesDir().getAbsolutePath();

        Log.e("paths", externalStor);
        Log.e("paths", internal);

//        getDirectories();

//        Log.e("Paths",getRemovableStoragePath());
//        File path = new File("/storage/");
//        File[] pathFiles = path.listFiles();

//        for (int i = 0; i < pathFiles.length; i++) {
//            Log.e("paths ", pathFiles[i].getName());
//        }
//
//        Log.e("Root", pathFiles.toString());

        initInternalMemory();

        initExternalMemory();

        initRamMemory();


    }

    private void initRamMemory() {
        final Runtime runtime = Runtime.getRuntime();
        final long usedMemory = (runtime.totalMemory() - runtime.freeMemory());
        final long maxHeapSize = runtime.maxMemory();
    }

    public static String getRemovableStoragePath() {

        File f = null;
//      Log.d(TAG, "Build.DEVICE: " + Build.DEVICE);
//      Log.d(TAG, "Build.MANUFACTURER: " + Build.MANUFACTURER);
//      Log.d(TAG, "Build.MODEL: " + Build.MODEL);

        if (Build.VERSION.RELEASE.startsWith("4")) {
            f = new File("/mnt/emmc");
        }
        if (Build.MODEL.toLowerCase().startsWith("mele")) {
            return "/mnt/extern_sd0";
        }
        if (Build.DEVICE.equals("nuclear-zoop") || Build.DEVICE.equals("nuclear-f900")) {
            f = new File("/mnt/extsd");
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String extendedPath = "";
        if (Build.DEVICE.toLowerCase().contains("samsung") || Build.MANUFACTURER.toLowerCase().contains("samsung")) {
            extendedPath = "/external_sd/";
            try {
                f = new File(path + extendedPath);
                if (f.exists() && f.isDirectory()) {
                    return f.getAbsolutePath();
                } else if (Build.MODEL.toLowerCase().contains("gt-i9300")) {
                    extendedPath = "/mnt/extSdCard/";
                    try {
                        f = new File(extendedPath);
                        if (f.exists() && f.isDirectory()) {
                            return f.getAbsolutePath();
                        }
                    } catch (Exception e) {
                        // continue execution
                    }
                } else {
                    extendedPath = "/sd";
                }
            } catch (Exception e) {
                // contine execution
            }
        } else if (Build.DEVICE.toLowerCase().contains("e0") || Build.MANUFACTURER.toLowerCase().contains("LGE")) {
            extendedPath = "/_ExternalSD/";
        } else if (Build.MANUFACTURER.toLowerCase().contains("motorola") || Build.DEVICE.toLowerCase().contains("olympus")) {
            f = new File("/mnt/sdcard-ext");
        }
        try {
            if (!extendedPath.equals("")) {
                f = new File(path + extendedPath);
            }
            if (f.exists() && f.isDirectory()) {
                //              Log.d(TAG, "path: " + f.getAbsolutePath());
                return f.getAbsolutePath();
            } else {
            }
        } catch (Exception e) {
            //          e.printStackTrace();
            // f is probably null. no need to print stacktrace.
            return path;
        }

        return path;
//      return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String[] getDirectories() {
        File tempFile;
        String[] directories = null;
        String[] splits;
        ArrayList<String> arrayList = new ArrayList<String>();
        BufferedReader bufferedReader = null;
        String lineRead;

        try {
            arrayList.clear(); // redundant, but what the hey
            bufferedReader = new BufferedReader(new FileReader("/proc/mounts"));

            while ((lineRead = bufferedReader.readLine()) != null) {

                splits = lineRead.split(" ");

                // System external storage
                if (splits[1].equals(Environment.getExternalStorageDirectory()
                        .getPath())) {
                    arrayList.add(splits[1]);
//                    MyLog.d(TAG, "gesd split 1: " + splits[1]);
                    continue;
                }

                // skip if not external storage device
                if (!splits[0].contains("/dev/block/")) {
                    continue;
                }

                // skip if mtdblock device

                if (splits[0].contains("/dev/block/mtdblock")) {
                    continue;
                }

                // skip if not in /mnt node

                if (!splits[1].contains("/mnt")) {
                    continue;
                }

                // skip these names

                if (splits[1].contains("/secure")) {
                    continue;
                }

                if (splits[1].contains("/mnt/asec")) {
                    continue;
                }

                // Eliminate if not a directory or fully accessible
                tempFile = new File(splits[1]);
                if (!tempFile.exists()) {
                    continue;
                }
                if (!tempFile.isDirectory()) {
                    continue;
                }
                if (!tempFile.canRead()) {
                    continue;
                }
                if (!tempFile.canWrite()) {
                    continue;
                }

                // Met all the criteria, assume sdcard
                arrayList.add(splits[1]);
            }

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
        }

        // Send list back to caller

        if (arrayList.size() == 0) {
            arrayList.add("sdcard not found");
        }
        directories = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            directories[i] = arrayList.get(i);
        }

        Log.e("directories",directories.toString());
        return directories;
    }


    private void initExternalMemory() {

        Toast.makeText(this, "" + Environment.getExternalStorageDirectory().getPath(), Toast.LENGTH_SHORT).show();
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        File file = new File(getSDcardDirectoryPath());

        Log.e("SD card Size", file.getFreeSpace() + "");
        Log.e("SD card Size", file.getTotalSpace() + "");

        if (isSDPresent) {
            Toast.makeText(this, "is available", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "not available", Toast.LENGTH_SHORT).show();
        }

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long sdAvailSize = (long) stat.getAvailableBlocks()
                * (long) stat.getBlockSize();

        Log.e("external memory with ", sdAvailSize + "");

        File externalFile = getExternalFilesDir(null);

        if (externalFile != null) {
            ProgressBar externalProgressBar = (ProgressBar) findViewById(R.id.pb_external);
            TextView textViewExternal = (TextView) findViewById(R.id.tv_external);

            long usedBytesExternal = externalFile.getUsableSpace();
            long totalBytesExternal = externalFile.getTotalSpace();
            Log.e("External memory", usedBytesExternal + "");
            Log.e("External memory", totalBytesExternal + "");

            externalProgressBar.setMax(getSpaceInMb(totalBytesExternal));
            externalProgressBar.setProgress(getSpaceInMb(usedBytesExternal));
            textViewExternal.setText(Utils.byteToReadable(usedBytesExternal) + "/" + Utils.byteToReadable(totalBytesExternal));

        }
    }

    private void initInternalMemory() {
        ProgressBar internalProgressBar = (ProgressBar) findViewById(R.id.pb_internal);
        TextView textViewInternal = (TextView) findViewById(R.id.tv_internal);

        long usedInternalSpace = new File(getFilesDir().getAbsoluteFile().toString()).getFreeSpace();
        long totalBytesInternal = new File(getFilesDir().getAbsoluteFile().toString()).getTotalSpace();

        usedInternalSpace = totalBytesInternal - usedInternalSpace;
        internalProgressBar.setMax(getSpaceInMb(totalBytesInternal));
        internalProgressBar.setProgress(getSpaceInMb(usedInternalSpace));
        textViewInternal.setText(Utils.byteToReadable(usedInternalSpace) + "/" + Utils.byteToReadable(totalBytesInternal));
    }

    private int getSpaceInMb(long totalBytesInternal) {
        long fileSizeInKB = totalBytesInternal / 1024;

        long fileSizeInMB = fileSizeInKB / 1024;
        return (int) fileSizeInMB;
    }




    public static HashSet<String> getExternalMounts() {
        final HashSet<String> out = new HashSet<String>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }


        Log.e("Memory path", out.toString());


        return out;
    }


    /**
     * Returns the SDcard storage path for samsung ex:- /storage/extSdCard
     *
     * @return
     */
    private String getSDcardDirectoryPath() {
        return System.getenv("SECONDARY_STORAGE");
    }


}
