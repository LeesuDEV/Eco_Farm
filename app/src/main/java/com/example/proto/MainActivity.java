package com.example.proto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText id, pw;
    Button login, register;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();                            //액션바 숨기기

        fAuth = FirebaseAuth.getInstance(); // Firebase Instance 초기화

        id = findViewById(R.id.id);
        pw = findViewById(R.id.pw);
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.registerBtn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getId = id.getText().toString();
                String getPw = pw.getText().toString();

                loginProcess(getId, getPw);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /*----------------------------------로그인 메소드---------------------------*/
    private void loginProcess(String id, String pw) {
        fAuth.signInWithEmailAndPassword(id, pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "환영합니다" + id + "님", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MainFragment.class);
                            String uid = fAuth.getCurrentUser().getUid();
                            intent.putExtra("uid", uid);  //인턴트에 uid삽입

                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
    /*----------------------------------로그인 메소드---------------------------*/
}