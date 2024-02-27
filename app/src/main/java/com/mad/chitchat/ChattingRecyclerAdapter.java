package com.mad.chitchat;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChattingRecyclerAdapter extends RecyclerView.Adapter<ChattingRecyclerAdapter.ChattingViewHolder>{

    private static final String TAG = "ChattingRecyclerAdapter";
    ArrayList<String> messageList,senderIdList,friendIdList;
//    int i = 0;
    private FirebaseAuth mAuth;

    public ChattingRecyclerAdapter(ArrayList<String> messageList, ArrayList<String> senderIdList, ArrayList<String> friendIdList){
        this.senderIdList = senderIdList;
        this.friendIdList = friendIdList;
        this.messageList = messageList;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ChattingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.chatting_layout,parent,false);
        ChattingViewHolder chattingViewHolder = new ChattingViewHolder(view);
        return chattingViewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ChattingViewHolder holder, int position) {
        if(senderIdList.get(position).equals(mAuth.getCurrentUser().getEmail().toString())){
            holder.userMessage.setText(messageList.get(position));
//            holder.friendMessage.setBackgroundColor(R.color.white);
//            holder.userMessage.setBackgroundColor(R.color.blue);
            holder.friendMessage.setVisibility(View.INVISIBLE);
            holder.userMessage.setVisibility(View.VISIBLE);
        }

        else if(friendIdList.get(position).equals(mAuth.getCurrentUser().getEmail().toString())){
            holder.friendMessage.setText(messageList.get(position));
//            holder.userMessage.setBackgroundColor(R.color.white);
//            holder.friendMessage.setBackgroundColor(R.color.black);
            holder.userMessage.setVisibility(View.INVISIBLE);
            holder.friendMessage.setVisibility(View.VISIBLE);
        }
//        Log.i(TAG,"Value of i:" + String.valueOf(i));
    }

    @Override
    public int getItemCount() {
//        i = 0;
        return messageList.size();
    }

    @Override
    public void onViewRecycled(ChattingViewHolder chattingViewHolder) {
        super.onViewRecycled(chattingViewHolder);

        chattingViewHolder.userMessage.setText(null);
        chattingViewHolder.friendMessage.setText(null);
    }

    class ChattingViewHolder extends RecyclerView.ViewHolder {

        private TextView userMessage,friendMessage;
        public ChattingViewHolder(View itemView){
            super(itemView);
            userMessage = itemView.findViewById(R.id.user_message);
            friendMessage = itemView.findViewById(R.id.friend_message);
        }

    }


    public interface OnUserClickListener{
        void onUserClick(View v);
    }

}
