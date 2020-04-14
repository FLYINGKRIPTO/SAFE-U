package com.womensafety.shajt3ch;

import android.Manifest.permission;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MyService extends Service implements SpeechDelegate, Speech.stopDueToDelay {

    public static SpeechDelegate delegate;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "STARTED BACKGROUND SERVICE", Toast.LENGTH_LONG).show();
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Speech.init(this);
        delegate = this;
        Speech.getInstance().setListener(this);

        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
            muteBeepSoundOfRecorder();
        } else {
            System.setProperty("rx.unsafe-disable", "True");
            RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) { // Always true pre-M
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        //showSpeechNotSupportedDialog();

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        //showEnableGoogleVoiceTyping();
                    }
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
                }
            });
            muteBeepSoundOfRecorder();
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        for (String partial : results) {
            Log.d("Result", partial + "");
        }
    }

    @Override
    public void onSpeechResult(String result) {


        Log.d("Result", result + "");
        if (!TextUtils.isEmpty(result)) {

            Toast.makeText(this, "You Said : " + result, Toast.LENGTH_SHORT).show();
            if (result.contains("help") || result.contains("bachao")) {
                //Toast.makeText(this, "Help mode activated ", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Help mode activated ", Toast.LENGTH_SHORT).show();


                SQLiteDatabase db2 = this.openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);

                GPSTracker gps;
                double latitude = 0,longitude = 0;
                gps = new GPSTracker(this);
                if(gps.canGetLocation()){
                    //   Log.d(TAG,"HERE uu ");
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    RGeocoder RGeocoder = new RGeocoder();
                    RGeocoder.getAddressFromLocation(latitude, longitude,getApplicationContext(), new HandlerForLocation());
                    Toast.makeText(this, "ll"+ latitude + "l" + longitude, Toast.LENGTH_SHORT).show();

                }
                else{
                    //    Log.d(TAG,"HERE");
                    gps.showSettingsAlert();
                }

                SmsManager smsManager = SmsManager.getDefault();
                for(int i=0; i< Register.getNumber(db2).size(); i++){
                    smsManager.sendTextMessage(Register.getNumber(db2).get(i), null, "Please HELP me !! I am in Danger, This is my current location" + "https://maps.google.com/?q="+latitude+","+longitude , null, null);
                }


                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Register.getNumber(db2).get(0)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                if (ActivityCompat.checkSelfPermission(this, permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);


                Toast.makeText(this, "Help mode activated ", Toast.LENGTH_SHORT).show();


            }
        }
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Speech.getInstance().isListening()) {
            muteBeepSoundOfRecorder();
            Speech.getInstance().stopListening();
        } else {
            RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) { // Always true pre-M
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        //showSpeechNotSupportedDialog();

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        //showEnableGoogleVoiceTyping();
                    }
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
                }
            });
            muteBeepSoundOfRecorder();
        }
    }

    private void muteBeepSoundOfRecorder() {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        PendingIntent service =
                PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
                        new Intent(getApplicationContext(), MyService.class), PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
        super.onTaskRemoved(rootIntent);
    }
}