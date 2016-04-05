package com.mikepenz.aboutlibraries.sample;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.entity.Library;

import java.util.List;

/**
 * An AsyncTask which iterates through every installed app and finds any that use a Library.
 * <p/>
 * Created by michaelcarrano on 8/30/14.
 */
public class DetectorAsyncTask extends AsyncTask<Void, Integer, Void> {

    private static Context mContext;

    private final Callbacks mCallbacks;

    private final PackageManager mPackageManager;

    private List<ApplicationInfo> mInstalledApplications;

    public DetectorAsyncTask(Context ctx, PackageManager packageManager, Callbacks callbacks) {
        mContext = ctx;
        mPackageManager = packageManager;
        mCallbacks = callbacks;
    }

    /**
     * Detects all libraries by trying to load the pattern classpath in a given Application
     */
    private static void detect(PackageInfo pkg) {
        Libs libs = new Libs(mContext);

        try {
            Log.e("test", "searching in: " + pkg.packageName);
            Context ctx = mContext.createPackageContext(pkg.packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);

            // Loop through known libraries
            for (Library library : libs.getLibraries()) {
                try {
                    Class<?> clazz = Class.forName(library.getClassPath(), false, ctx.getClassLoader());

                    // Detected a library!!!
                    if (clazz != null) {
                        Log.e("test", "---- library found: " + library.getLibraryName());
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // 0: scan system apps, 1: do not scan system apps
        for (int i = 0; i < mInstalledApplications.size(); i++) {
            ApplicationInfo appInfo = mInstalledApplications.get(i);
            publishProgress(mInstalledApplications.size(), i);
            try {
                PackageInfo pkgInfo = mPackageManager.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS);
                detect(pkgInfo);

            } catch (PackageManager.NameNotFoundException e) {
                Log.i(this.getClass().getSimpleName(), "doInBackground: " + e.toString());
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        mInstalledApplications = mPackageManager.getInstalledApplications(0);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mCallbacks.onProgressUpdate(progress[0], progress[1], mPackageManager.getApplicationLabel(
                mInstalledApplications.get(progress[1])));
    }

    @Override
    protected void onPostExecute(Void sources) {
        mCallbacks.onTaskFinished();
    }

    public interface Callbacks {

        void onTaskFinished();

        void onProgressUpdate(int max, int percent, CharSequence app);
    }

}