package com.womensafety.shajt3ch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PanicTimePicker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PanicTimePicker extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TimePicker timePicker;
    private Button btnPanic, scheduleTask;

    public PanicTimePicker() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimePicker.
     */
    // TODO: Rename and change types and number of parameters
    public static PanicTimePicker newInstance(String param1, String param2) {
        PanicTimePicker fragment = new PanicTimePicker();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_picker, container, false);
        timePicker = view.findViewById(R.id.timePickerP);
        btnPanic = view.findViewById(R.id.btPanicP);
        scheduleTask = view.findViewById(R.id.btScheduleP);

        btnPanic.setOnClickListener((v) -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                container.getContext().startService(new Intent(container.getContext(), FloatingViewService.class));
            } else if (Settings.canDrawOverlays(container.getContext())) {
                container.getContext().startService(new Intent(container.getContext(), FloatingViewService.class));
            } else {
                askPermission(container.getContext());
                Toast.makeText(container.getContext(), "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
            }
        });
        scheduleTask.setOnClickListener((v) -> {
            Calendar calendar = Calendar.getInstance();
            if (Build.VERSION.SDK_INT >= 23) {

                calendar.set(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        timePicker.getHour(),
                        timePicker.getMinute(),
                        0
                );
            } else {
                calendar.set(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute(),
                        0
                );
            }
            setAlarm(calendar.getTimeInMillis(), container.getContext());
        });

        return  view;
    }

    private void askPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" +  context.getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    private void setAlarm(long timeInMillis, Context context) {

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, PanicAlarm.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
            Toast.makeText(context, "Panic TIME is set ", Toast.LENGTH_SHORT).show();

    }

}
