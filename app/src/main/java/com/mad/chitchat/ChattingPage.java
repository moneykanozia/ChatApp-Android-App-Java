package com.mad.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChattingPage extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "ChattingPage";
    private Toolbar toolbar;
    private ImageView friendImage;
    private TextView friendName;
    private RecyclerView chattingRecyclerView;
    private ChattingRecyclerAdapter chattingRecyclerAdapter;
    private ViewGroup.LayoutParams params;
    private ImageButton sendMessage,backToUsersPage;
    private EditText messageBox;
    private FirebaseAuth mAuth;
    private ArrayList<String> message_list,senderIdList,friendIdList;
    FirebaseFirestore db;
    String combinedId, _userEmail,_friendEmail;
    int totalDocuments = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_page);
        toolbar = (Toolbar)findViewById(R.id.chatting_page_toolbar);
        setSupportActionBar(toolbar);
        Bundle friendData = getIntent().getExtras();
        friendImage = (ImageView)findViewById(R.id.friend_image);
        friendName = (TextView)findViewById(R.id.friend_email);
        chattingRecyclerView = (RecyclerView)findViewById(R.id.chatting_recycler_view);
        sendMessage = (ImageButton)findViewById(R.id.send_message);
        backToUsersPage = (ImageButton)findViewById(R.id.back_arrow);
        messageBox = (EditText)findViewById(R.id.message_box);
        TextView friendEmail = (TextView) findViewById(R.id.friend_email);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        message_list = new ArrayList<>();
        senderIdList = new ArrayList<>();
        friendIdList = new ArrayList<>();
        chattingRecyclerAdapter = new ChattingRecyclerAdapter(message_list,senderIdList,friendIdList);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Bitmap bitmap = (Bitmap)friendData.getParcelable("friendImage");
        friendName.setText(friendData.getString("friendName"));
        friendImage.setImageBitmap(bitmap);
        _userEmail = mAuth.getCurrentUser().getEmail().toString();
        _friendEmail = friendEmail.getText().toString();
        combinedId = _userEmail.length()>_friendEmail.length() ? _friendEmail + "__" + _userEmail : _userEmail + "__" + _friendEmail ;
//        db.collection("chatting").document(combinedId).collection("messages").document("1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if(!documentSnapshot.exists()){
//                    Map<String,Object> messageData = new HashMap<>();
//                    messageData.put("messageText","");
//                    messageData.put("senderId","");
//                    messageData.put("friendId","");
//                    db.collection("chatting").document(combinedId).collection("messages").document("1").set(messageData)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
////                        Toast.makeText(ChattingPage.this, "success", Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(ChattingPage.this, "failure", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//            }
//        });
        chattingRecyclerView.setAdapter(chattingRecyclerAdapter);
        sendMessage.setOnClickListener(this);
        backToUsersPage.setOnClickListener(this);
        messageBox.setOnFocusChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        db.collection("chatting").document(combinedId).collection("messages").document("1").addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
////                if(e!=null){
////                    Log.i(TAG,"message is: "+e.toString());
////                }
////                else{
////                    Log.i(TAG,"message is: " + "nothing");
////                }
//                if (documentSnapshot.exists()){
//                    String messageText = documentSnapshot.getString("messageText");
//                    Toast.makeText(ChattingPage.this, messageText, Toast.LENGTH_SHORT).show();
////                    Toast.makeText(ChattingPage.this, "onStart method", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(ChattingPage.this, "onStart method else", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        db.collection("chatting").document(combinedId).collection("messages").orderBy("messageNumber").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
//                Log.i(TAG,"message is: " + String.valueOf(querySnapshot.size()));
//                if(querySnapshot.size() == 1){
//                    for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
//                        String messageText = queryDocumentSnapshot.getString("messageText");
//                        String senderId = queryDocumentSnapshot.getString("senderId");
//                        String friendId = queryDocumentSnapshot.getString("friendId");
//                        if(!messageText.equals("")){
//                            totalDocuments = 1;
//                            senderIdList.add(senderId);
//                            friendIdList.add(friendId);
//                            message_list.add(messageText);
//                            chattingRecyclerAdapter.notifyItemInserted(0);
//                        }
////                        Toast.makeText(ChattingPage.this, messageText, Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else {
//                senderIdList.clear();
//                friendIdList.clear();
//                message_list.clear();
//                    int i = 0;
                if (totalDocuments != querySnapshot.size()){
                    totalDocuments = querySnapshot.size();
                }
                else{
                    totalDocuments = querySnapshot.size() - 1;
                }
                if (message_list.isEmpty() ) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                        String senderId = queryDocumentSnapshot.getString("senderId");
                        String friendId = queryDocumentSnapshot.getString("friendId");
                        String messageText = queryDocumentSnapshot.getString("messageText");
                        senderIdList.add(senderId);
                        friendIdList.add(friendId);
                        message_list.add(messageText);
//                        i++;
//                        Toast.makeText(ChattingPage.this, messageText, Toast.LENGTH_SHORT).show();
                    }
                    chattingRecyclerAdapter.notifyItemRangeChanged(0,totalDocuments);
                    chattingRecyclerView.smoothScrollToPosition(chattingRecyclerView.getBottom());
                }
                else{
                    if (totalDocuments == querySnapshot.size()) {
                        List<DocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
                        DocumentSnapshot documentSnapshot = documentSnapshotList.get(totalDocuments - 1);
                        String senderId = documentSnapshot.getString("senderId");
                        String friendId = documentSnapshot.getString("friendId");
                        String messageText = documentSnapshot.getString("messageText");
                        senderIdList.add(senderId);
                        friendIdList.add(friendId);
                        message_list.add(messageText);
                        chattingRecyclerAdapter.notifyItemInserted(totalDocuments - 1);
                    }
                    else{
                        totalDocuments = querySnapshot.size();
                    }
                }
//                chattingRecyclerView.smoothScrollToPosition(chattingRecyclerView.getBottom());
            }
//            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M) //This statement is for getSystemService() method.
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send_message) {
            String messageText = messageBox.getText().toString();
            if (!messageText.equals("")) {
                messageBox.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // For closing keyboard
                imm.hideSoftInputFromWindow(messageBox.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
//            message_list.add(messageText);
//            chattingRecyclerAdapter.notifyItemInserted(message_list.size()-1);
                sendMessageMethod(messageText, _userEmail, _friendEmail);
            }
        }
        else{
            startActivity(new Intent(this,UsersPage.class));
            finishActivity(1);
        }
    }

    void sendMessageMethod(String messageText,String _userEmail,String _friendEmail){
        Map<String,Object> messageData = new HashMap<>();
        Map<String,Object> temp = new HashMap<>();
        messageData.put("messageText",messageText);
        messageData.put("senderId",_userEmail);
        messageData.put("friendId",_friendEmail);
        messageData.put("messageNumber",totalDocuments + 1);
        temp.put("chat","");
        db.collection("chatting").document(combinedId).set(temp);
        db.collection("chatting").document(combinedId).collection("messages").document().set(messageData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(ChattingPage.this, "success", Toast.LENGTH_SHORT).show();
                        chattingRecyclerView.smoothScrollToPosition(chattingRecyclerView.getBottom());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChattingPage.this, "failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            //got focus
//            chattingRecyclerView.smoothScrollToPosition(totalDocuments - 1);
        }
    }
}