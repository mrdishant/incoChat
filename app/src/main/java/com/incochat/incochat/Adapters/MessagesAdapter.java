package com.incochat.incochat.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.incochat.incochat.Activities.ChatActivity;
import com.incochat.incochat.Activities.ImageActivity;
import com.incochat.incochat.R;
import com.incochat.incochat.beans.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mrdis on 1/31/2018.
 */


public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<Message> messages;
    public ArrayList<String> delete;
    ImageView deleteButton;
    Context context;
    String deleteChatId;
    boolean deleteEnabled=false;

    public void setDeleteChatId(String deleteChatId) {
        this.deleteChatId = deleteChatId;
    }

    public void setDeleteButton(ImageView deleteButton) {
        this.deleteButton = deleteButton;

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context,"Delete Clicked",Toast.LENGTH_LONG).show();

                if(deleteChatId==null|| deleteChatId.equals("")){
                    Toast.makeText(context,"Chat error",Toast.LENGTH_LONG).show();
                }

                for(String id:delete){
                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            .collection("Chats").document()
                            .collection("messages").document(id)
                            .delete();
                }
            }
        });

    }

    public MessagesAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        delete=new ArrayList<>();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==0){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message,parent,false);
            return new SentMessage(view);
        }else{
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message1,parent,false);
            return new RecievedMessage(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(messages.get(position).isUsersent()){
            ((SentMessage)holder).bind(messages.get(position));
            ((SentMessage) holder).cardView2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    delete.add(messages.get(position).getId());
                    ((SentMessage) holder).cardView2.setAlpha(0.5f);
                    deleteButton.setVisibility(View.VISIBLE);
                    deleteEnabled=true;
                    return true;
                }
            });

            ((SentMessage) holder).cardView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(deleteEnabled){
                        delete.add(messages.get(position).getId());
                        ((SentMessage) holder).cardView2.setAlpha(0.5f);
                    }
                }
            });

            ((SentMessage) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,ImageActivity.class);
                    intent.putExtra("url",messages.get(position).getUrl());
                    intent.putExtra("viewable",true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        }else{
            ((RecievedMessage)holder).bind(messages.get(position));
            ((RecievedMessage) holder).cardView1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    delete.add(messages.get(position).getId());
                    ((RecievedMessage) holder).cardView1.setAlpha(0.5f);
                    deleteButton.setVisibility(View.VISIBLE);
                    deleteEnabled=true;
                    return true;
                }
            });

            ((RecievedMessage) holder).cardView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(deleteEnabled){
                        delete.add(messages.get(position).getId());
                        ((RecievedMessage) holder).cardView1.setAlpha(0.5f);
                    }
                }
            });

            ((RecievedMessage) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,ImageActivity.class);
                    intent.putExtra("url",messages.get(position).getUrl());
                    intent.putExtra("viewable",true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {

        if(messages.get(position).isUsersent()){
            return 0;
        }else{
            return 1;
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class SentMessage extends RecyclerView.ViewHolder{

        LinearLayout cardView2;
        TextView messagetext2,time2;
        ImageView imageView;

        public SentMessage(View itemView) {
            super(itemView);
            cardView2=itemView.findViewById(R.id.messagecard2);
            messagetext2=(TextView) itemView.findViewById(R.id.messagetext2);
            time2=(TextView) itemView.findViewById(R.id.time2);
            imageView=(ImageView)itemView.findViewById(R.id.imageview);
        }

        void bind(Message message){
            if(message.getUrl()!=null){
                imageView.setVisibility(View.VISIBLE);
                messagetext2.setVisibility(View.GONE);
                Picasso.get().load(message.getUrl()).fit().centerCrop().into(imageView);
            }else{
                imageView.setVisibility(View.GONE);
                messagetext2.setVisibility(View.VISIBLE);
            }
            messagetext2.setText(message.getText());
            time2.setText(message.getTime());
        }

    }

    class RecievedMessage extends RecyclerView.ViewHolder{

        LinearLayout cardView1;
        TextView messagetext1,time1;
        ImageView imageView;

        public RecievedMessage(View itemView) {
            super(itemView);
            cardView1=itemView.findViewById(R.id.messagecard1);
            messagetext1=(TextView) itemView.findViewById(R.id.messagetext1);
            time1=(TextView) itemView.findViewById(R.id.time1);
            imageView=(ImageView)itemView.findViewById(R.id.imageview);
        }


        void bind(Message message){

            if(message.getUrl()!=null){
                imageView.setVisibility(View.VISIBLE);
                messagetext1.setVisibility(View.GONE);
                Picasso.get().load(message.getUrl()).fit().centerCrop().into(imageView);
            }else{
                imageView.setVisibility(View.GONE);
                messagetext1.setVisibility(View.VISIBLE);
            }
            messagetext1.setText(message.getText());
            time1.setText(message.getTime());
        }

    }


    public void delete(){
        if(deleteChatId==null|| deleteChatId.equals("")){
            Toast.makeText(context,"Chat error",Toast.LENGTH_LONG).show();
        }

        for(String id:delete){
            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .collection("Chats").document()
                    .collection("messages").document(id)
                    .delete();
        }
    }



}
