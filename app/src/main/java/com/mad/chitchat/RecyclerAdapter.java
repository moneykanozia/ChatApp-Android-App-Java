package com.mad.chitchat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
//    private static final String TAG = "RecyclerAdapter";
//    int count = 0;
    ArrayList<Friends> friendsData;
    private RecyclerAdapter.OnUserClickListener onUserClickListener;

    RecyclerAdapter(ArrayList<Friends> friendsData, RecyclerAdapter.OnUserClickListener onUserClickListener){
        this.friendsData = friendsData;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        Log.i(TAG,"onCreateViewHolder" + count++);
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.list_items,viewGroup,false);
        MyViewHolder myViewHolder = new MyViewHolder(view,onUserClickListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.email.setText(friendsData.get(i).getUname());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(friendsData.get(i).getImageUrl());
        final long ONE_MEGABYTE = 5000000;
//        download file as a byte array
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                myViewHolder.userImage.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsData.size();
    }

    class MyViewHolder extends ViewHolder implements View.OnClickListener{

        private OnUserClickListener onUserClickListener;
        private ImageView userImage;
        private TextView email,lastMessage;
        public MyViewHolder(View itemView, RecyclerAdapter.OnUserClickListener onUserClickListener){
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            email = itemView.findViewById(R.id.uname);
            lastMessage = itemView.findViewById(R.id.last_message);
            itemView.setOnClickListener(this);
            this.onUserClickListener = onUserClickListener;
        }

        @Override
        public void onClick(View v) {
            onUserClickListener.onUserClick(v);
        }
    }

    public interface OnUserClickListener{
        void onUserClick(View v);
    }
}
