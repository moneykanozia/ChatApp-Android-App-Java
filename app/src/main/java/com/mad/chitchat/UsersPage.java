package com.mad.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UsersPage extends AppCompatActivity implements View.OnClickListener, SearchRecyclerAdapter.OnUserClickListener, RecyclerAdapter.OnUserClickListener{
    private EditText searchbox;
    private Button usersSearch;
    private RecyclerView recyclerView,searchRecyclerView;
    private RecyclerAdapter recyclerAdapter;
    private SearchRecyclerAdapter searchRecyclerAdapter;
    private ViewGroup.LayoutParams params;
    private String userEmail;
    ArrayList<Friends> friendsList;
    private  ArrayList<String> friendsId = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_page);
        ActionBar actionBar = getSupportActionBar();
        searchbox = (EditText)findViewById(R.id.search_box);
        usersSearch = (Button)findViewById(R.id.users_search);
        searchRecyclerView = (RecyclerView) findViewById(R.id.searched_users__recyclerView);
        recyclerView = (RecyclerView)findViewById(R.id.added_users__recyclerView);
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        params = searchRecyclerView.getLayoutParams();
        friendsList = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(friendsList,UsersPage.this);
        usersSearch.setOnClickListener(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(recyclerAdapter);
//        Toast.makeText(UsersPage.this, "Create Method", Toast.LENGTH_SHORT).show();
//        search.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                String text = search.getText().toString().trim();
//                Toast.makeText(UsersPage.this,text,Toast.LENGTH_SHORT).show();
////                searchUser(text);
//                return false;
//            }
//        });
//        usersSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String text = usersSearch.getText().toString().trim();
////                Toast.makeText(UsersPage.this,text,Toast.LENGTH_SHORT).show();
//                if(!text.equals("")){
//                    searchUser(text);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("chatting").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        String combinedId,idsArray[];
                        friendsId.clear();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                            combinedId = queryDocumentSnapshot.getId();
                            idsArray = combinedId.split("__");
                            if (idsArray[0].equals(userEmail)){
                                friendsId.add(idsArray[1]);
                            }
                            else if(idsArray[1].equals(userEmail)){
                                friendsId.add(idsArray[0]);
                            }
                        }
                        if(!friendsId.isEmpty()){
                            createFriendsData();
                        }
                        else{
                            Toast.makeText(UsersPage.this, "else", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UsersPage.this, "onFailure", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,LoginPage.class));
                finishActivity(1);
        }
        return  super.onOptionsItemSelected(item);
    }


    public void onClick(View v){
        if (!searchbox.getText().toString().equals("")) {
            searchRecyclerView.setVisibility(View.VISIBLE);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("users").orderBy("email").startAt(searchbox.getText().toString()).endAt(searchbox.getText().toString() + '\uf8ff').get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            ArrayList<SearchUsers> searchUsersList = new ArrayList<>();
                            params.height = 400;
                            searchRecyclerView.setLayoutParams(params);
                            String uname = "", imageUrl = "";
                            int i = 0;
                            boolean currentUser = false;
                            SearchUsers tempSearchUser;
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                uname = documentSnapshot.getString("email");
                                if (userEmail.equals(uname)){
                                    currentUser = true;
                                }
                                if(!currentUser){
                                    imageUrl = documentSnapshot.getString("imageUrl");
                                    tempSearchUser = new SearchUsers();
                                    tempSearchUser.setUname(uname);
                                    tempSearchUser.setImageUrl(imageUrl);
                                    searchUsersList.add(tempSearchUser);
                                }
                            }
                            searchRecyclerAdapter = new SearchRecyclerAdapter(searchUsersList,UsersPage.this);
                            searchRecyclerView.setLayoutManager(new LinearLayoutManager(UsersPage.this));
                            searchRecyclerView.setAdapter(searchRecyclerAdapter);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UsersPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            params.height = 1;
            searchRecyclerView.setLayoutParams(params);
        }
    }

    public void onUserClick(View v){
        Intent goToChattingPage = new Intent(this,ChattingPage.class);
        TextView friendName = v.findViewById(R.id.uname);
        ImageView friendImage = v.findViewById(R.id.user_image);
        friendImage.buildDrawingCache();
        Bitmap bitmap = friendImage.getDrawingCache();
        goToChattingPage.putExtra("friendName",friendName.getText().toString());
        goToChattingPage.putExtra("friendImage", bitmap);
        params.height = 1;
        searchRecyclerView.setLayoutParams(params);
        searchbox.setText("");
        startActivity(goToChattingPage);
       finishActivity(1);
    }

    void searchUser(String text){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereLessThanOrEqualTo("email",text).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        String data = "";
//                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
//                            data += documentSnapshot.getString("uname");
//                        }
                        Toast.makeText(UsersPage.this, String.valueOf(queryDocumentSnapshots.size()), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(UsersPage.this,"money",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UsersPage.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    void createFriendsData(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("users").orderBy("email").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String uname = "", imageUrl = "";
                            int i = 0;
                            Friends tempFriend;
                            friendsList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                uname = documentSnapshot.getString("email");
                                if (friendsId.contains(uname)) {
                                    imageUrl = documentSnapshot.getString("imageUrl");
                                     tempFriend = new Friends();
                                     tempFriend.setUname(uname);
                                     tempFriend.setImageUrl(imageUrl);
                                    friendsList.add(tempFriend);
                                    i++;
                                }
                            }
                            recyclerAdapter.notifyDataSetChanged();
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UsersPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
    }

}