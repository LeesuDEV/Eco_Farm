package com.example.proto;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.SimpleFormatter;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private EditText emailET, passwordET, passwordConfirmET, nickNameET;
    private Button registerBtn, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        passwordConfirmET = findViewById(R.id.passwordConfirmET);
        nickNameET = findViewById(R.id.nickNameET);
        registerBtn = findViewById(R.id.registerBtn);
        backBtn = findViewById(R.id.backBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //계정생성 시도
                createAccount(
                        emailET.getText().toString(),
                        passwordET.getText().toString(),
                        passwordConfirmET.getText().toString()
                );
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 화면으로 다시 돌아가기
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createAccount(String email, String password, String passwordConfirm) {

        // 이메일,비번 유효성검사
        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //가입성공시
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser(); // FAuth 에서 유저데이터 받아오기.
                            createUserTable(user.getUid());
                            Toast.makeText(RegisterActivity.this, user.getEmail().toString() + "님, 가입을 축하합니다!",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            //이미 아이디가 존재할 시
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "이미 존재하는 계정입니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailET.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Email은 필수요소입니다.", Toast.LENGTH_SHORT).show();
            emailET.setError("Required");
            valid = false;
        } else {
            emailET.setError(null);
        }

        String password = passwordET.getText().toString();
        String passwordConfirm = passwordConfirmET.getText().toString();
        if (TextUtils.isEmpty(password) && TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, "Password는 필수요소입니다.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            //비밀번호와 비밀번호확인이 일치하지 않을때
            if (!(password.equals(passwordConfirm))) {
                Toast.makeText(RegisterActivity.this, "비번을 확인해주세요. 일치하지않습니다.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            passwordET.setError(null);
            passwordConfirmET.setError(null);
        }

        String nickName = nickNameET.getText().toString();
        if (nickName.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "nickName은 필수요소입니다.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            if(nickName.length() > 8){
                Toast.makeText(RegisterActivity.this, "nickName은 8글자를 넘을 수 없습니다.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            nickNameET.setError(null);
        }

        return valid;
    }

    private void createUserTable(String uid) {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        DefaultData defaultValue = new DefaultData(0, 0, 0, 0);

        firebaseDatabase.child("USER").child(uid).child("now").setValue(defaultValue);
        firebaseDatabase.child("USER").child(uid).child("hope").setValue(defaultValue)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "데이터가 성공적으로 생성됐습니다.");
                        } else {
                            Log.w(TAG, "데이터가 생성 실패.", task.getException());
                        }
                    }
                });
        Map<String,Object> data = new HashMap<>();
        data.put("CROPS","default"); // 작물상태
        data.put("STATUS",false); // 작물상태
        data.put("DATE", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())); // 오늘날짜
        data.put("EXPIRE_DATE", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())); // 오늘날짜
        data.put("USER_NAME",nickNameET.getText().toString());

        FirebaseFirestore firstoreDatabase = FirebaseFirestore.getInstance();
        firstoreDatabase.collection("users").document(uid).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DB데이터가 성공적으로 생성됐습니다.");
            }
        });
    }

    class DefaultData {
        public int temp;
        public int hum;
        public int water_level;
        public int brightness;
        public DefaultData() {
        }

        public DefaultData(int temp, int hum, int water_level, int brightness) {
            this.temp = temp;
            this.hum = hum;
            this.water_level = water_level;
            this.brightness = brightness;
        }
    }
}
