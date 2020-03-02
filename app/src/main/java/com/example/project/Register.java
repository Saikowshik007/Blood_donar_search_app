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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {
    Button register;
    EditText namef,phonef,emailf,passwordf,agef,groupf;
    TextView login;
    FirebaseAuth fa;
    ProgressBar pb;
    String userId,email, password, phone, name,age,group;
    FirebaseFirestore fs;
    public boolean validate(String name,String email,String password,String phone){
        if(TextUtils.isEmpty(name)){ namef.setError("Enter a valid name");return false; }
        if (TextUtils.isEmpty(email)||!email.contains("@")||!email.contains(".")) { emailf.setError("Valid email is required"); return false;}
        if(TextUtils.isEmpty(phone)||phone.length()!=10){phonef.setError("Enter a valid Phone");return false;}
        if(TextUtils.isEmpty(password)||password.length()<=8||password.matches("A-z0-9")){passwordf.setError("Enter a valid password");return false;}
        else {return true;}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        login=findViewById(R.id.link_login);
        namef=findViewById(R.id.editText3);
        emailf=findViewById(R.id.editText4);
        phonef=findViewById(R.id.editText6);
        agef=findViewById(R.id.age);
        groupf=findViewById(R.id.group);
        fa=FirebaseAuth.getInstance();
        pb=findViewById(R.id.progressBar2);
        passwordf=findViewById(R.id.editText5);
        register=findViewById(R.id.button2);
        fs=FirebaseFirestore.getInstance();

        if(fa.getCurrentUser()!=null){startActivity(new Intent(Register.this,MainActivity.class));finish();}
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passwordf.onEditorAction(EditorInfo.IME_ACTION_DONE);
                name = namef.getText().toString().trim();
                email = emailf.getText().toString().trim();
                password = passwordf.getText().toString().trim();
                phone = phonef.getText().toString().trim();
                age=agef.getText().toString().trim();
                group=groupf.getText().toString().trim();

                if(validate(name,email,password,phone)){
                pb.setVisibility(View.VISIBLE);
                fa.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            userId=fa.getCurrentUser().getUid();
                            User user1=new User(name,phone,email,group,age,userId);
                            Toast.makeText(Register.this, "Authentication passed.", Toast.LENGTH_SHORT).show();
                            CollectionReference cf=fs.collection("Users");
                            cf.add(user1);
                            finish();

                        } else {

                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                        startActivity(new Intent(Register.this,MainActivity.class));
                        overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                        finish();
                    }
                });
            }}

            });

                login.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                startActivity(new Intent(Register.this, Login.class));
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                finish();
            }
            });

        }

    }
