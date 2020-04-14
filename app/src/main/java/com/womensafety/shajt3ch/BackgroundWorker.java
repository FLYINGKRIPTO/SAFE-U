package com.womensafety.shajt3ch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackgroundWorker extends Worker {
    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        displayNotification("FROM SAFE U ","YOUR PANIC TIME IS ON");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getApplicationContext().startService(new Intent(getApplicationContext(), FloatingViewService.class));
        } else if (Settings.canDrawOverlays(getApplicationContext())) {
           getApplicationContext().startService(new Intent(getApplicationContext(), FloatingViewService.class));
        }
        return Result.Success.success();
    }

    private void displayNotification(String task, String description) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Panic ", "SAFE U ", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);

        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Panic").setContentTitle(task).setContentText(description)
                .setSmallIcon(R.mipmap.ic_launcher_round);
        manager.notify(1,builder.build());
    }
}
