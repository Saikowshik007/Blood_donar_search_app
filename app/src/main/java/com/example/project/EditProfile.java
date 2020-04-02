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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class EditProfile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Button update;
    ImageButton back;
    CountryCodePicker countryCodePicker;
    EditText namef, phonef, emailf, agef;
    Spinner groupf;
    TextView login, location,dt;
    FirebaseAuth fa;
    ProgressBar pb;
    boolean otp;
    Switch available;
    String email,phone, name, age, group, city,date;
    FirebaseFirestore fs;
    String[] groups = {"O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-"};
    private String mVerificationId;

    public boolean validate(String name, String email, String phone, String city) {
        if (city == null) {
            location.setError("Turn on Location");
            Toast.makeText(EditProfile.this, "Enable Location to continue", Toast.LENGTH_SHORT).show();
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

        if (TextUtils.isEmpty(phone)) {
            phonef.setError("Enter a valid Phone");
            return false;


        }
        else {
            return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);
        countryCodePicker = findViewById(R.id.eccp);
        city = MainActivity.getActivityInstance().getData();
        location = findViewById(R.id.etextView10);
        namef = findViewById(R.id.eeditText3);
        emailf = findViewById(R.id.eeditText4);
        phonef = findViewById(R.id.eeditText6);
        agef = findViewById(R.id.eage);
        groupf = findViewById(R.id.espinner);
        dt=findViewById(R.id.editText8);
        fa = FirebaseAuth.getInstance();
        otp=false;
       back=findViewById(R.id.back);
        available=findViewById(R.id.switch2);
        pb = findViewById(R.id.eprogressBar2);
        update = findViewById(R.id.ebutton2);
        date=Home.currentuser.get(0).getDate();
        fs = FirebaseFirestore.getInstance();
        setdata();
        ArrayAdapter<String> groupa = new ArrayAdapter<String>(EditProfile.this, R.layout.support_simple_spinner_dropdown_item, groups);
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
            public void onNothingSelected(AdapterView<?> parent) { }});
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                name = namef.getText().toString().trim();
                email = emailf.getText().toString().trim();
                phone = "+"+countryCodePicker.getSelectedCountryCode() + phonef.getText().toString().trim();
                age = agef.getText().toString().trim();
                v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();

                if (validate(name, email, phone, city)) {
                    update();
                   //sendVerificationCode(phone);
                }
            }
        });

        dt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdate();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this,Profile.class));finish();}});

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month+=1;
      date=""+dayOfMonth+"-"+month+"-"+year;
        dt.setText(date);

    }
    public void showdate(){
        DatePickerDialog datePickerDialog=new DatePickerDialog(this,this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    public void update(){
        DocumentReference ref=fs.collection("Users").document(Home.docid);
        ref.update("name",name,"phone",phone,"age",age,"location",city,"group",group,"email",email,"date",date,"availability",available.isChecked()?"1":"0").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
           Toast.makeText(EditProfile.this,"success",Toast.LENGTH_SHORT).show();
           Home.listAdapter.notifyDataSetChanged();
           finish();
                }
                else{Toast.makeText(EditProfile.this,"Failed due to "+task.getException(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(EditProfile.this,"Failed due to "+fs.collection("Users").document().getId(),Toast.LENGTH_SHORT).show();}
            }
        });

    }
    void sendEmail(){
        fa.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EditProfile.this,"Verification Emial Sent Succesfully ",Toast.LENGTH_SHORT).show();
            }
        });
    }
    void setnewpassword(String newpassword){
        fa.getCurrentUser().updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
    void updateEmail(String newEmail){
        sendEmail();
        if(fa.getCurrentUser().isEmailVerified()){
        fa.getCurrentUser().updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
    }
    public void setdata(){
        namef.setText(Home.currentuser.get(0).getName());
        emailf.setText(Home.currentuser.get(0).getEmail());
        phonef.setText(Home.currentuser.get(0).getPhone().replace("+"+countryCodePicker.getSelectedCountryCode(),""));
        agef.setText(Home.currentuser.get(0).getAge());
        location.setText("Last registered Location" + " : " + city);
        dt.setText("day-month-year:\n"+date);
                if(Home.currentuser.get(0).getAvailability().contains("1"))
                    available.setChecked(true);
                else
                    available.setChecked(false);
                //Toast.makeText(EditProfile.this,""+Home.currentuser.get(0).getAvailability(),Toast.LENGTH_SHORT).show();
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
                verify(code);
                otp=true;
               // update();
                pb.setVisibility(View.INVISIBLE);
            }
            else{otp=false;}
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            Toast.makeText(EditProfile.this, "Auto-Fetching OTP", Toast.LENGTH_LONG).show();
        }

        private void verify(String code) {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, code);

        }
        };


}
