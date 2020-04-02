package com.example.project;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity  {
    EditText emailf, passwordf;
    Button login;
    ProgressBar pb;
    FirebaseAuth fa;
    TextView link_signup,forgot;
    private AlphaAnimation anim =new AlphaAnimation(1F,0.8F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if(!checkPermissions()){requestPermissions();}
        emailf = findViewById(R.id.editText);
        passwordf = findViewById(R.id.editText2);
        link_signup=findViewById(R.id.link_signup);
        login = findViewById(R.id.button);
        forgot=findViewById(R.id.reset);
        pb = findViewById(R.id.progressBar);
        fa = FirebaseAuth.getInstance();
        final MainActivity ma=MainActivity.getActivityInstance();
        if(fa.getCurrentUser()!=null){startActivity(new Intent(Login.this,Home.class));}
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailf.getText().toString().trim();
                String password = passwordf.getText().toString().trim();
                pb.setVisibility(View.VISIBLE);
                v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
               // v.getBackground().clearColorFilter();


                if (TextUtils.isEmpty(email)||!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
                    emailf.setError("Invalid Email");
                    emailf.requestFocus();
                    return;
                }
                if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                    passwordf.setError("password must contain special symbols,numbers and letters combination");
                    passwordf.requestFocus();
                    return;
                }
                if(!ma.Lflag){
                    Toast.makeText(Login.this, "Turn on location", Toast.LENGTH_LONG).show();
                    ma.ex.run();pb.setVisibility(View.INVISIBLE);}
                else if(ma.getData()!=null){
                    fa.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(Login.this, Home.class));
                            overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                            finish();
                            ma.stop=true;
                        } else {
                            Toast.makeText(Login.this, "User not found or No Internet Connectivity.",
                                    Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.INVISIBLE);
                        }

                    }

                });
                }
            }

        });
        link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                if(!ma.Lflag){
                    Toast.makeText(Login.this, "Turn on location", Toast.LENGTH_LONG).show();
                    ma.ex.run();pb.setVisibility(View.INVISIBLE);}
                else if(ma.getData()!=null){
                    signup();
                }

            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotpassword();
            }
        });

    }
    public void signup(){
       startActivity(new Intent(Login.this,Register.class));
        overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
        finish();
        }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    protected void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                44
        );
    }
    public void forgotpassword(){
        if (TextUtils.isEmpty(emailf.getText().toString())) {
            emailf.setError("Invalid Email");
            emailf.requestFocus();
            Toast.makeText(Login.this,"Enter a valid email to continue",Toast.LENGTH_LONG).show();
        }
        else{
            pb.setVisibility(View.VISIBLE);
            fa.sendPasswordResetEmail(emailf.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Login.this,"Check your email to reset your account.",Toast.LENGTH_LONG).show();
                        pb.setVisibility(View.INVISIBLE);

                    }
                    else{
                        Toast.makeText(Login.this,"Cannot generate reset link."+task.getException(),Toast.LENGTH_LONG).show();
                        pb.setVisibility(View.INVISIBLE);
                    }

                }
            });
        }
    }

}