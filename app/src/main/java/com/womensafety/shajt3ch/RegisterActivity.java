package com.womensafety.shajt3ch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailText,passwordText,phoneNumberText,userNameText;
    private Button registerBtn, loginBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTheme(R.style.AppTheme);
        mAuth = FirebaseAuth.getInstance()  ;
        initializeUI();
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
       registerBtn.setOnClickListener(v -> {
           registerNewUser();
       });
       loginBtn.setOnClickListener(v -> {
           Intent intent = new Intent(this,LoginActivity.class);
           startActivity(intent);
       });
       if(mAuth.getCurrentUser() != null){
           Intent intent = new Intent(this,MainActivity.class);
           startActivity(intent);
       }
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email , password, phoneNumber, username;
        email = emailText.getText().toString();
        username = userNameText.getText().toString();
        password = passwordText.getText().toString();
        phoneNumber = phoneNumberText.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Please Enter Email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Please Enter Password",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(getApplicationContext(),"Please Enter PhoneNumber",Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    String userID = user.getUid();
                    reference  = FirebaseDatabase.getInstance().getReference("users").child(userID);
                    HashMap<String,String> hashMap  = new HashMap<>();
                    hashMap.put("id",userID);
                    hashMap.put("username",username);
                    hashMap.put("phone",phoneNumber);
                    hashMap.put("email",email);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                }
                else {
                    Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }


    private void initializeUI() {
        userNameText = findViewById(R.id.username);
        emailText= findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        phoneNumberText = findViewById(R.id.phone_number);
        registerBtn = findViewById(R.id.register);
        loginBtn = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);
    }
}
