package com.example.easycheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeScreen extends AppCompatActivity {

    //Login Button.
    private Button login_btn;
    //Email and Password Edit texts.
    private EditText email_txt,password_txt;
    //Label for the not registered users to get them to register activity.
    private TextView not_register_txtview;
    //Firebase instance
    private FirebaseAuth mAuth;

    static String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        //Initialize firebase instance
        mAuth = FirebaseAuth.getInstance();
        //Find the views in the activity FrontEnd
        login_btn = findViewById(R.id.login_btn);
        email_txt = findViewById(R.id.editTextUserEmail);
        password_txt = findViewById(R.id.editTextTextPassword);
        not_register_txtview = findViewById(R.id.register_lbl);


        not_register_txtview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CreateAccount.class));
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_details()){
                    signIn(email_txt.getText().toString(),password_txt.getText().toString());
                }
            }
        });

    }

    boolean check_details(){
        System.out.println("Cheking Values...");
        boolean correct = true;
        if(email_txt.getText().toString().isEmpty()){
            email_txt.setError(getString(R.string.email_error_message));
            correct = false;
        }
        if(password_txt.getText().toString().isEmpty()){
            password_txt.setError(getString(R.string.password_error_message));
            correct = false;
        }
        return correct;
    }

        private void signIn(String email, String password) {
            System.out.println("Tring to login to system using email : "+email+" password : "+password);
            // [START sign_in_with_email]
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getApplicationContext(),"Loged in succeffully",Toast.LENGTH_LONG).show();
                                Log.d("LOGINSUCCESSFUL", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                uuid = user.getUid();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                            else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(),"Failed to login",Toast.LENGTH_LONG).show();
                                Log.w("LOGINFAILED", "signInWithEmail:failure", task.getException());
                                Toast.makeText(WelcomeScreen.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            // [END sign_in_with_email]
        }
/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }*/
}