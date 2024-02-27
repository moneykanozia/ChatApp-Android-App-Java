package com.mad.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;

public class ProfilePage extends AppCompatActivity implements View.OnClickListener{
    private CardView cardView;
    private ImageView image;
    private FloatingActionButton change_image;
    private EditText uname,email,dob,status;
    private ImageButton calender;
    private Button submit;
    private String user_email;
    private TextView profile_message;
    private Uri uri;
    private FirebaseStorage fireStorage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        fireStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        cardView = (CardView)findViewById(R.id.cardView);
        image = (ImageView)findViewById(R.id.image);
        change_image = (FloatingActionButton)findViewById(R.id.btn_change_image);
        Bundle b = getIntent().getExtras();
        user_email = b.getString("email");
        uname = (EditText)findViewById(R.id.username);
        dob = (EditText)findViewById(R.id.dob);
        calender = (ImageButton)findViewById(R.id.calender);
        status = (EditText)findViewById(R.id.status);
        email = (EditText)findViewById(R.id.email);
        submit = (Button)findViewById(R.id.submit_profile);
        profile_message = (TextView)findViewById(R.id.profile_message);
        change_image.setOnClickListener(this);
        calender.setOnClickListener(this);
        submit.setOnClickListener(this);
        email.setText(user_email);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v){
        if(v == calender){
            DatePickerDialog date_picker_dialog = new DatePickerDialog(this);
            date_picker_dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    dob.setText(dayOfMonth + "/" + month + 1 + "/" + year);
                }
            });
            date_picker_dialog.show();
        }
        else if(v == submit){
           fieldsValidation();
        }
        else{
            ImagePicker.with(this).crop()
                    .start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            uri = data.getData();
            if(uri != null){
                image.setImageURI(uri);
                image.setTag("Non-default");
            }
            else{
                image.setTag("default");
                image.setImageResource(R.drawable.profile_image);
            }
    }

    void fieldsValidation(){
        if (uname.getText().toString().equals("")) {
            uname.setError("enter the username");
            uname.requestFocus();
        }
        else if(dob.getText().toString().equals("")){
            dob.setError("enter the dob");
            dob.requestFocus();
        }
        else if(status.getText().toString().equals("")){
            status.setError("enter the status");
            status.requestFocus();
        }
        else if(image.getTag().equals("default")){
            image.requestFocus();
            profile_message.setVisibility(View.VISIBLE);
        }
        else{
            //store data to firestore
            saveProfileImage(uri);
//            startActivity(new Intent(ProfilePage.this,UsersPage.class));
        }
    }

    void saveProfileImage(Uri uri){
        StorageReference fileRef = fireStorage.getReference("images").child(String.valueOf(System.currentTimeMillis()));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri downloadUrl)
                    {
                        //do something with downloadurl
                        saveProfileData(uname.getText().toString(), email.getText().toString(), dob.getText().toString(), status.getText().toString(), downloadUrl.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showErrorOnFailure(e);            }
        });
    }

//    String getImageFileExtention(Uri uri){ // This function returning null instead of string
//        ContentResolver cr = getContentResolver();
//        MimeTypeMap  mtp = MimeTypeMap.getSingleton();
//        return mtp.getExtensionFromMimeType(cr.getType(uri));
//    }

    void saveProfileData(String uname, String email, String dob, String status, String imageUrl){
        Map<String,Object> map = new HashMap<>();
        map.put("uname",uname);
        map.put("email",email);
        map.put("dob",dob);
        map.put("status",status);
        map.put("imageUrl",imageUrl);
        firestore.collection("users").document(email).set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        profile_message.setVisibility(View.INVISIBLE);
                        Toast.makeText(ProfilePage.this,"Profile Created", Toast.LENGTH_SHORT).show();
                        Intent goToUsersPage = new Intent(ProfilePage.this,UsersPage.class);
                        goToUsersPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(goToUsersPage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showErrorOnFailure(e);
                    }
                });
    }

    private void showErrorOnFailure(Exception e){
        if(e.getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")){
            Toast.makeText(ProfilePage.this,"No internet connection",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(ProfilePage.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}