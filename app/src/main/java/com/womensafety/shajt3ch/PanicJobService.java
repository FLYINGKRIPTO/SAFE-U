package com.womensafety.shajt3ch;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PanicJobService extends JobService {
    private static final String TAG = "PanicJobService";
    private boolean jobCancelled = false;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"jpb started");
        getApplicationContext().startService(new Intent(getApplicationContext(), FloatingViewService.class));
        jobFinished(params,false);
        return true;
    }

//    private void doBackgroundWork(JobParameters params) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                if(jobCancelled) return;
//                Log.d(TAG,"RUN ")
//;                getApplicationContext().startService(new Intent(getApplicationContext(), FloatingViewService.class));
//
//                jobFinished(params,false);
//            }
//        });
//    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"job cancelled");
        jobCancelled = true;
        return false;
    }
}
