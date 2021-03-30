package com.example.group8_inclass12;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth uAuth;
    Boolean flag;

    Button btn_signup, btn_cancel;
    EditText et_fname, et_lname, et_signupemail, et_signuppassword, et_confpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign Up");

        et_fname = findViewById(R.id.et_fname);
        et_lname = findViewById(R.id.et_lname);
        et_signupemail = findViewById(R.id.et_signupemail);
        et_signuppassword = findViewById(R.id.et_signuppassword);
        et_confpassword = findViewById(R.id.et_confpassword);
        btn_signup = findViewById(R.id.btn_signuppage);
        btn_cancel = findViewById(R.id.btn_cancel);

        uAuth = FirebaseAuth.getInstance();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //also here add the validations for password and confpassword and others

                if (et_fname.getText().toString().equals("")) {
                    et_fname.setError("Enter a valid First Name");
                }
                else if (et_lname.getText().toString().equals("")) {
                    et_lname.setError("Enter a valid Last Name");
                } else if (et_signupemail.getText().toString().equals("")) {
                    et_signupemail.setError("Enter a valid email ID");
                }
                else if (et_signuppassword.getText().toString().equals("")) {
                    et_signuppassword.setError("Enter a valid password");
                }
                else if(et_confpassword.getText().toString().equals("")) {
                    et_confpassword.setError("Enter a valid password");
                }
                else if (!(et_signuppassword.getText().toString()).equals(et_confpassword.getText().toString())) {
                    et_confpassword.setError("Passwords do not match");
                }
                else {
                    uAuth.createUserWithEmailAndPassword(et_signupemail.getText().toString(), et_signuppassword.getText().toString())
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("demo", "createUserWithEmail:success");
                                        flag = true;
                                        Intent toContactList = new Intent(SignUp.this, ContactList.class);
                                        Toast.makeText(getApplicationContext(), "User has been created!", Toast.LENGTH_SHORT).show();
                                        toContactList.putExtra("NEWUSER", flag);
                                        startActivity(toContactList);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.e("demo", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(SignUp.this, MainActivity.class);
                startActivity(login);
                finish();

            }
        });
    }
}
