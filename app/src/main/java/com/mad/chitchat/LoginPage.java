package com.mad.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText email,paswd;
    private Button login,create_profile;
    private TextView message,signUp;
    private int flag = 0;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        email = (EditText)findViewById(R.id.login_email);
        paswd = (EditText)findViewById(R.id.login_paswd);
        signUp = (TextView)findViewById(R.id.login_signUp);
        message = (TextView)findViewById(R.id.login_message);
        login = (Button)findViewById(R.id.btn_login);
        create_profile = (Button)findViewById(R.id.create_profile);
        login.setOnClickListener(this);
        signUp.setOnClickListener(this);
        create_profile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == signUp){
            Intent goToRegisterPage = new Intent(LoginPage.this, RegisterPage.class);
            email.setText("");
            paswd.setText("");
            message.setVisibility(View.INVISIBLE);
            startActivity(goToRegisterPage);
        }
        else if(v == create_profile){
            String email = this.email.getText().toString();
            Intent goToProfilePage = new Intent(LoginPage.this,ProfilePage.class);
            goToProfilePage.putExtra("email",email);
            goToProfilePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goToProfilePage);
        }
        else{
            validate(email.getText().toString(), paswd.getText().toString());
        }

    }

    @Override
    public void onStart(){
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
                // Go to users Page
//            Toast.makeText(LoginPage.this,"Already Logged in",Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this,UsersPage.class));
//           finishActivity(1);
//            }
        }

    private void validate(String email,String paswd){
        if(email.equals("")){
            this.email.setError("Enter the email");
            this.email.requestFocus();
        }
        else if(paswd.equals("")){
            this.paswd.setError("Enter the password");
            this.paswd.requestFocus();
        }
        else{
            logIn(email,paswd);
        }
    }

    private void logIn(String email,String paswd){
        mAuth.signInWithEmailAndPassword(email,paswd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user.isEmailVerified()){
                        flag = 0;
                        checkProfileCreated(email);
                    }
                    else{
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    flag = 1;
                                    Toast.makeText(LoginPage.this,"Verify your email and Create your profile",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    if(flag==1){
                                        Toast.makeText(LoginPage.this,"Verify your email and Create your profile",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
                else{
                    if(task.getException().getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")){
                        Toast.makeText(LoginPage.this,"No internet connection",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginPage.this,"Invalid Username or Password",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkProfileCreated(String email){
        DocumentReference doc_ref = db.collection("users").document(email);
        doc_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Intent goToUsersPage = new Intent(LoginPage.this,UsersPage.class);
                    goToUsersPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // Go to users Page
                    startActivity(goToUsersPage);
                   finishActivity(1);
                }
                else{
                    Button create_profile = (Button)findViewById(R.id.create_profile);
                    Toast.makeText(LoginPage.this,"Create your profile First",Toast.LENGTH_SHORT).show();
                    create_profile.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}