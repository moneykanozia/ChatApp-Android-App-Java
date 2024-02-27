package com.mad.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterPage extends AppCompatActivity implements View.OnClickListener {
    private EditText email,paswd;
    private FirebaseAuth mAuth;
    private TextView message,login;
    private Button register;
    private Intent goToLoginPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        mAuth = FirebaseAuth.getInstance();
        email = (EditText)findViewById(R.id.register_email);
        paswd = (EditText)findViewById(R.id.register_password);
        register = (Button)findViewById(R.id.btn_register);
        login = (TextView)findViewById(R.id.login_login);
        message = (TextView)findViewById(R.id.register_message);
        goToLoginPage = new Intent(this,LoginPage.class);
        goToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(v == register){
            validate(email.getText().toString(), paswd.getText().toString());
        }
        else{
            startActivity(goToLoginPage);
        }
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
            createUser(email,paswd);
        }
    }

    private void createUser(String email,String paswd){
        EditText field_email_ref = this.email,
                field_paswd_ref = this.paswd;
        mAuth.createUserWithEmailAndPassword(email,paswd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){
                                 field_email_ref.setText("");
                                 field_paswd_ref.setText("");
                                 message.setVisibility(View.INVISIBLE);
                                 Toast.makeText(RegisterPage.this,"Registeration Successful. Please check your email to verify",Toast.LENGTH_SHORT).show();
                                 startActivity(goToLoginPage);
                             }
                             else{
                                 showErrorOnFailure(task.getException());
                             }
                        }
                    });
                }
                else{
                    showErrorOnFailure(task.getException());
                }
            }
        });
    }

    private void showErrorOnFailure(Exception e){
        if(e.getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")){
            Toast.makeText(RegisterPage.this,"No internet connection",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(RegisterPage.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}