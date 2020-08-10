package com.example.project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Button register;
    CountryCodePicker countryCodePicker;
    EditText namef, phonef, emailf, passwordf, agef;
    Spinner groupf;
    TextView login, location,dt;
    FirebaseAuth fa;
    ProgressBar pb;
    String userId, email, password, phone, name, age, group, city, otp,date;
    FirebaseFirestore fs;
    MainActivity ma;
    String[] groups = {"O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-"};
    private String mVerificationId;
    public boolean validate(String name, String email, String password, String phone, String city) {
        if (city == null) {
            location.setError("Turn on Location");
            Toast.makeText(Register.this, "Enable Location to continue", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            namef.setError("Enter a valid name");
            namef.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email) || !email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            emailf.setError("Invalid Email");
            emailf.requestFocus();
            return false;
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            passwordf.setError("password must contain special symbols,numbers and letters combination");
            passwordf.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            phonef.setError("Enter a valid Phone");
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        countryCodePicker = findViewById(R.id.ccp);
        city = MainActivity.getActivityInstance().getData();
        login = findViewById(R.id.link_login);
        location = findViewById(R.id.textView10);
        namef = findViewById(R.id.editText3);
        emailf = findViewById(R.id.editText4);
        phonef = findViewById(R.id.editText6);
        dt=findViewById(R.id.textView17);
        agef = findViewById(R.id.age);
        groupf = findViewById(R.id.spinner);
        fa = FirebaseAuth.getInstance();
        pb = findViewById(R.id.progressBar2);
        passwordf = findViewById(R.id.editText5);
        register = findViewById(R.id.button2);
        fs = FirebaseFirestore.getInstance();
        ma=MainActivity.getActivityInstance();
        location.setText("Current Location" + " : " + city);
        ArrayAdapter<String> groupa = new ArrayAdapter<String>(Register.this, R.layout.support_simple_spinner_dropdown_item, groups);
        groupf.setAdapter(groupa);
        groupf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        group = "O+";
                        break;
                    case 1:
                        group = "A+";
                        break;
                    case 2:
                        group = "B+";
                        break;
                    case 3:
                        group = "AB+";
                        break;
                    case 4:
                        group = "O-";
                        break;
                    case 5:
                        group = "A-";
                        break;
                    case 6:
                        group = "B-";
                        break;
                    case 7:
                        group = "AB-";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        if (fa.getCurrentUser() != null) {
            startActivity(new Intent(Register.this, MainActivity.class));
            finish();
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = namef.getText().toString().trim();
                email = emailf.getText().toString().trim();
                password = passwordf.getText().toString().trim();
                phone = "+"+countryCodePicker.getSelectedCountryCode() + phonef.getText().toString().trim();
                age = agef.getText().toString().trim();
                v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();

                if (validate(name, email, password, phone, city)) {
                    sendVerificationCode(phone);
                    pb.setVisibility(View.VISIBLE);

                    }
                }


        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        });
        dt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdate();

            }
        });

    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                Toast.makeText(Register.this,""+code,Toast.LENGTH_LONG);
                verify(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            Toast.makeText(Register.this,"Auto-Fetching OTP",Toast.LENGTH_LONG).show();
           // mResendToken = forceResendingToken;
        }
    };
    private void verify(String code){
        PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(mVerificationId,code);
        createaccount();
    }
    public void createaccount(){
        fa.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    pb.setVisibility(View.INVISIBLE);
                    userId = fa.getCurrentUser().getUid();
                    User user1 = new User(name, phone, email, group, age, userId, city,date,"1",Double.toString(ma.lat),Double.toString(ma.lon));
                    Toast.makeText(Register.this, "Authentication passed.", Toast.LENGTH_SHORT).show();
                    DocumentReference cf = fs.collection("Users").document(fa.getCurrentUser().getUid());
                    cf.set(user1);
                    finish();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();

                    fa.getCurrentUser().updateProfile(profileUpdates);

                } else {

                    Toast.makeText(Register.this, "Authentication failed."+task.getException(), Toast.LENGTH_LONG).show();
                }
                startActivity(new Intent(Register.this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month+=1;
      date=""+dayOfMonth+"-"+month+"-"+year;
        dt.setText("Selected date is:"+date);

    }
    public void showdate(){
        DatePickerDialog datePickerDialog=new DatePickerDialog(this,this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    }
