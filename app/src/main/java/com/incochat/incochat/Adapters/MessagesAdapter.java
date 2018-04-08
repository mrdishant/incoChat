package com.incochat.incochat.Adapters;

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
    Context context;

    public MessagesAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(messages.get(position).isUsersent()){
            ((SentMessage)holder).bind(messages.get(position));
            ((SentMessage) holder).cardView2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(context,"Available soon",Toast.LENGTH_LONG).show();
                    return true;
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
                    Toast.makeText(context,"Available soon1",Toast.LENGTH_LONG).show();
                    return true;
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



}
