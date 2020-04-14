package com.womensafety.shajt3ch;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class Register extends Fragment implements View.OnClickListener {
    TextInputLayout namelayout, numberlayout;
    private static EditText name, number;
    public SQLiteDatabase db;
    public static SQLiteDatabase db2;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view = inflater.inflate(R.layout.contacts_register, container, false);
        namelayout = view.findViewById(R.id.name);
        numberlayout = view.findViewById(R.id.mobile);
        name = view.findViewById(R.id.editText2);
        number = view.findViewById(R.id.editText3);
        Button save = view.findViewById(R.id.save);
        save.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getActivity(), "save started", Toast.LENGTH_LONG).show();

       /* name.setHint("Person name");
        number.setHint("Mobile Number");*/
        String str_name = name.getText().toString();
        String str_number = number.getText().toString();
        PhoneNumber.phoneNumber = str_number;
        Log.d("Phone", PhoneNumber.phoneNumber);
        db = getActivity().openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);
        //Toast.makeText(getApplicationContext(), "db created",Toast.LENGTH_LONG).show();
        //db2 = db;
        db.execSQL("CREATE TABLE IF NOT EXISTS details(Pname VARCHAR,number VARCHAR);");
        //Toast.makeText(getApplicationContext(), "table created",Toast.LENGTH_LONG).show();

        Cursor c = db.rawQuery("SELECT * FROM details", null);
        if (c.getCount() < 6) {
            db.execSQL("INSERT INTO details VALUES('" + str_name + "','" + str_number + "');");
            Toast.makeText(getActivity(), "Successfully Saved" + str_name + " : " + str_number, Toast.LENGTH_SHORT).show();
            name.getText().clear();
            number.getText().clear();

        } else {
            Toast.makeText(getActivity(), "You can add only 5 contacts", Toast.LENGTH_SHORT).show();
        }


        db.close();


    }



    public static ArrayList<String> getNumber(SQLiteDatabase db2) {


        Cursor c = null;
        String phone_num = "";
        //db2 = openOrCreateDatabase("NumDB", MODE_PRIVATE, null);
        ArrayList<String> numbers = new ArrayList<String>();
        c = db2.rawQuery("SELECT * FROM details LIMIT 3", null);
        if (c.getCount() > 0) {
            //c.moveToFirst();
            Log.d("Register", String.valueOf(c.getCount()));
            while (c.moveToNext()) {
                numbers.add(c.getString(1));
                phone_num += c.getString(1);
            }

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            numbers.forEach((n) -> Log.d("Register", n));
        }
        return numbers;


    }


}

