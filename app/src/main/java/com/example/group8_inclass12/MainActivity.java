/*
Name: Vaishali Krishnamurthy, Nivedita Veeramanigandan
 Student ID: 801077752, 801151512
 */

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
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth uAuth;
    Button btn_login, btn_signup;
    EditText et_loginemail, et_loginpassword;
//    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Boolean flag;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = uAuth.getCurrentUser();
        if(currentUser!=null){
            Log.d("demo", "Current User: "+currentUser.getEmail());

        }else{
            Log.d("demo", "Not found user, login!!");
        }    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Login");
        et_loginemail = findViewById(R.id.et_loginemail);
        et_loginpassword = findViewById(R.id.et_loginpassword);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);

        uAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_loginemail.getText().toString().equals("")) {
                    et_loginemail.setError("Enter a valid email ID");
                }
                else if (et_loginpassword.getText().toString().equals("")) {
                    et_loginpassword.setError("Enter a valid password");
                }
                else{
                    uAuth.signInWithEmailAndPassword(et_loginemail.getText().toString(), et_loginpassword.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("demo", "signInWithEmail:success");
                                        FirebaseUser user = uAuth.getCurrentUser();
                                        //intent and recycler view code comes here
                                        flag = false;
                                        Intent toContactList = new Intent(MainActivity.this, ContactList.class);
                                        toContactList.putExtra("NEWUSER", flag);
                                        startActivity(toContactList);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("demo", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(MainActivity.this, SignUp.class);
                startActivity(signup);
                finish();
            }
        });
    }
}
