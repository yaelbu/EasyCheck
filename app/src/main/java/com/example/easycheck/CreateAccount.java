package com.example.easycheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class CreateAccount extends AppCompatActivity {


    EditText first_name_txt,last_name_txt,email_txt,password_txt;
    Button signup_btn;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        first_name_txt = findViewById(R.id.first_name_txt);
        last_name_txt = findViewById(R.id.last_name_txt);
        email_txt = findViewById(R.id.email_txt);
        password_txt = findViewById(R.id.password_txt);
        signup_btn = findViewById(R.id.signup_btn);




        signup_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //get all values from fields.
                final String first_name = first_name_txt.getText().toString().trim();
                final String last_name = last_name_txt.getText().toString().trim();
                final String Email = email_txt.getText().toString().trim();
                final String Password = password_txt.getText().toString().trim();

                if(!check_details()){
                    return;
                }

                HashMap<String,Object> new_user = new HashMap<>();

                new_user.put("firstname",first_name);
                new_user.put("lastname",last_name);
                new_user.put("Email",Email);
                new_user.put("password",Password);

                createAccount(Email,Password,new_user);


            }
        });



    }

    boolean check_details(){
        boolean flag=true;
        if(first_name_txt.getText().toString().isEmpty()){
            first_name_txt.setError(getString(R.string.first_name_field_error));
            flag = false;
        }
        if(last_name_txt.getText().toString().isEmpty()){
            last_name_txt.setError(getString(R.string.last_name_field_error));
            flag = false;
        }
        if(email_txt.getText().toString().isEmpty()){
            email_txt.setError(getString(R.string.email_field_error));
            flag = false;
        }
        if(password_txt.getText().toString().isEmpty()){
            password_txt.setError(getString(R.string.password_error_message));
            flag = false;
        }
        if(checkPasswordStrength()){

        }

        return flag;
    }

    private void createAccount(String email, String password, HashMap<String,Object> user) {
        try{
        HashMap<String,Object> new_user = new HashMap<>();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uuid = mAuth.getCurrentUser().getUid();
                            // Sign in success, update UI with the signed-in user's information
                            db.collection("users").document(uuid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    System.out.println("User has been added to FireStore.");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Error adding the data to FireStore.");
                                }
                            });
                            Toast.makeText(getApplicationContext(),"User Created Successfully.",Toast.LENGTH_LONG).show();
                            Log.d("USERCREATED", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            System.out.println(user.getEmail());
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("USERFAILED", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
        }
        catch (Exception e){

        }
    }

    boolean checkPasswordStrength(){
        if(password_txt.getText().toString().length()<6){
            password_txt.setError(getString(R.string.password_length_error));
            return false;
        }
        return true;
    }
}