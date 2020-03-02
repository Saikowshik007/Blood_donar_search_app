package com.example.project;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText emailf, passwordf;
    Button login;
    ProgressBar pb;
    FirebaseAuth fa;
    TextView link_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        emailf = findViewById(R.id.editText);
        passwordf = findViewById(R.id.editText2);
        link_signup=findViewById(R.id.link_signup);
        login = findViewById(R.id.button);
        pb = findViewById(R.id.progressBar);
        fa = FirebaseAuth.getInstance();
        if(fa.getCurrentUser()!=null){startActivity(new Intent(Login.this,MainActivity.class));}
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordf.onEditorAction(EditorInfo.IME_ACTION_DONE);
                String email = emailf.getText().toString().trim();
                String password = passwordf.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailf.setError("Inavlid Email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordf.setError("Select a good password");
                    return;
                }
                if (password.length() <= 8) {
                    passwordf.setError("Password lenght must be greater than 8");
                    return;
                }
                pb.setVisibility(View.VISIBLE);
                fa.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(Login.this, MainActivity.class));
                            overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "User not found.",
                                    Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.INVISIBLE);
                        }

                    }
                });
            }

        });
        link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                finish();
            }
        });


    }
}