package com.example.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class Profile  extends AppCompatActivity{
    String pname,pnum,email,group,age,userId,location,password;
    TextView a, b,c,d,e,f;
    ImageButton back;
    Dialog mdialog;
    EditText emailf,passwordf;
    FirebaseUser user;
    FloatingActionButton floatingActionButton,delete;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        a = findViewById(R.id.my_name);
        b = findViewById(R.id.my_emailid);
        c = findViewById(R.id.my_blood_group);
        d = findViewById(R.id.my_location);
        f = findViewById(R.id.last_donated);
        password="saikowshik@1";
        floatingActionButton=findViewById(R.id.floatingActionButton);
        delete=findViewById(R.id.floatingActionButton2);
        e = findViewById(R.id.my_phone);
        setData();
        back = findViewById(R.id.imageButton5);
        mdialog=new Dialog(Profile.this);
        mdialog.setContentView(R.layout.passwordfrag);
back.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(Profile.this, Home.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
    }
});
floatingActionButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(Profile.this,EditProfile.class));
    }
});
delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
mdialog.show();
Button confirm,close;
emailf=mdialog.findViewById(R.id.email);
passwordf=mdialog.findViewById(R.id.password);
confirm=mdialog.findViewById(R.id.button4);
close=mdialog.findViewById(R.id.button3);
close.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) { mdialog.cancel(); }});
confirm.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        email=emailf.getText().toString();
        password=passwordf.getText().toString();
        reauthenticate();}});
    }});
    }
    public void setData(){
        a.setText(Home.currentuser.get(0).getName());
        b.setText(Home.currentuser.get(0).getEmail());
        c.setText(Home.currentuser.get(0).getGroup());
        d.setText(Home.currentuser.get(0).getLocation());
        e.setText(Home.currentuser.get(0).getPhone());
        f.setText(Home.currentuser.get(0).getDate());
    }
    void deletedata() {
        FirebaseFirestore.getInstance().collection("Users").document(Home.docid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Profile.this, "deleted data", Toast.LENGTH_SHORT).show();
                    deleteuser();
                } else {
                    Toast.makeText(Profile.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void deleteuser(){
        FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Profile.this,"deleted",Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(Profile.this,Login.class));
                //finish();
                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void reauthenticate(){
        if(!email.isEmpty()&&!password.isEmpty()) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                deletedata();
                            } else {
                                mdialog.cancel();
                                emailf.setText("");passwordf.setText("");emailf.clearFocus();
                                Toast.makeText(Profile.this, "deleted" + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{emailf.setError("This field cannot be empty.");emailf.requestFocus();}
    }
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(Profile.this, Home.class));
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        super.onBackPressed();
    }

}
