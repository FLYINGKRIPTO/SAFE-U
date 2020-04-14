package com.womensafety.shajt3ch;
import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class FloatingViewService extends  Service implements View.OnClickListener {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;

    public  FloatingViewService(){}
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutCollapsed:
                break;
            case R.id.layoutExpanded:
                //switching views
                startService(new Intent(FloatingViewService.this, MyService.class));
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);

                SQLiteDatabase db2 = this.openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);

                Log.d("Number is:", Register.getNumber(db2).get(0));
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
                    smsManager.sendTextMessage(Register.getNumber(db2).get(i), null, "Please HELP me !! I am in Danger, This is my current location " + "https://maps.google.com/?q="+latitude+","+longitude , null, null);
                }


                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Register.getNumber(db2).get(0)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
                break;

            case R.id.buttonClose:
                //closing the widget
                stopSelf();
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        assert mWindowManager != null;
        mWindowManager.addView(mFloatingView, params);


        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

        //adding click listener to close button and expanded view
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
        expandedView.setOnClickListener(this);

        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        //when the drag is ended switching the state of the widget
                        collapsedView.setVisibility(View.GONE);
                        expandedView.setVisibility(View.VISIBLE);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}
