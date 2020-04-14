package com.womensafety.shajt3ch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.codemybrainsout.ratingdialog.RatingDialog;

import static com.womensafety.shajt3ch.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    public static Boolean isActive = false;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private Button btStartService, btnPanic, scheduleTask;
    private TextView tvText;
    private TimePicker timePicker;
    private ShareActionProvider mShareActionProvider;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        //   OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(BackgroundWorker.class).build();

//        btStartService = (Button) findViewById(R.id.btStartService);
//        tvText = (TextView) findViewById(R.id.tvText);

        enableAutoStart();
//        btnPanic = findViewById(R.id.btPanic);
//        scheduleTask = findViewById(R.id.btSchedule);
//
//        timePicker = findViewById(R.id.timePicker);
        if (checkServiceRunning()) {
            btStartService.setText(getString(R.string.stop_service));
            tvText.setVisibility(View.VISIBLE);
        }

//        btStartService.setOnClickListener(v -> {
//            if (btStartService.getText().toString().equalsIgnoreCase(getString(R.string.start_service))) {
//                startService(new Intent(MainActivity.this, MyService.class));
//                btStartService.setText(getString(R.string.stop_service));
//                tvText.setVisibility(View.VISIBLE);
//            } else {
//                stopService(new Intent(MainActivity.this, MyService.class));
//                btStartService.setText(getString(R.string.start_service));
//                tvText.setVisibility(View.GONE);
//            }
//        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedScreen(R.id.nav_main);
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Asking user if explanation is needed

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            return false;
        } else {
            return true;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_power:
                androidx.appcompat.app.ActionBar bar = getSupportActionBar();
                //item.setTitle("changed");

                Log.d("Title:", item.getTitle().toString());

                if (item.getTitle().toString().contains("START")) {
                    Log.d("Title2:", "If er vitore");

                    SQLiteDatabase db2 = this.openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);

                    Log.d("Number is:", Register.getNumber(db2).get(0));

                    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#78C257")));
                    startService(new Intent(MainActivity.this, MyService.class));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        startService(new Intent(MainActivity.this, FloatingViewService.class));
                    } else if (Settings.canDrawOverlays(MainActivity.this)) {
                        startService(new Intent(MainActivity.this, FloatingViewService.class));
                    } else {
                        askPermission(MainActivity.this);
                        Toast.makeText(MainActivity.this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
                    }

                    item.setTitle("STOP SERVICE");

                    return true;
                } else {
                    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E81123")));
                    startService(new Intent(MainActivity.this, MyService.class));

                    item.setTitle("START SERVICE");
                    return true;
                }


/*
                    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#439EB8")));
                    stopService(new Intent(MainActivity.this, MyService.class));
                    return true;
*/
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        displaySelectedScreen(R.id.nav_main);
        return true;
    }
    private void askPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" +  context.getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean checkServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                    Integer.MAX_VALUE)) {
                if (getString(R.string.my_service_name).equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void enableAutoStart() {
        for (Intent intent : Constants.AUTO_START_INTENTS) {
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                new MaterialDialog.Builder(this).title(R.string.enable_autostart)
                        .content(R.string.ask_permission)
                        .theme(Theme.LIGHT)
                        .positiveText(getString(R.string.allow))
                        .onPositive((dialog, which) -> {
                            try {
                                for (Intent intent1 : Constants.AUTO_START_INTENTS)
                                    if (getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY)
                                            != null) {
                                        startActivity(intent1);
                                        break;
                                    }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .show();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.newbutton, menu);

        // Locate MenuItem with ShareActionProvider
        // MenuItem item = menu.findItem(R.id.menu_share);

        // Fetch and store ShareActionProvider
        //mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //setIntent("Testing Share Feature");
        // Return true to display menu

        return true;
    }

    // Call to update the share intent
    private void setIntent(String s) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_TEXT, s);
        mShareActionProvider.setShareIntent(intent);

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_main: {
                fragment = new MainView();
                break;
            }
            case R.id.nav_inst: {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.popup_layout, null);
                AlertDialog alertDialog = new AlertDialog.Builder(
                        MainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Instructions");
                // Setting Dialog Message
                alertDialog.setView(alertLayout);
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.instruct_icon);
                // Showing Alert Message
                alertDialog.show();
            }
            break;
            case R.id.nav_verify:
                fragment = new Verify();
                break;
            case R.id.nav_register:
                fragment = new Register();
                break;
            case R.id.nav_display:
                fragment = new Display();
                break;
            case R.id.nav_nearby:
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_rate:
                final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                        .threshold(3)
                        .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                            @Override
                            public void onFormSubmitted(String feedback) {

                            }
                        }).build();

                ratingDialog.show();
                break;
            case R.id.nav_safety:
                Intent intent = new Intent(MainActivity.this, Safety.class);
                startActivity(intent);
                break;
            case R.id.panic_button:
                fragment = new PanicTimePicker();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

    }


}

class HandlerForLocation extends Handler {


}
