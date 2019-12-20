package com.newtoncy.group_project.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtoncy.group_project.R;
import com.newtoncy.group_project.javaclass.ImgInfo;

import java.util.ArrayList;
import java.util.List;

public class ImgListAdapter extends RecyclerView.Adapter {
    List<ImgInfo> imgInfoList = new ArrayList<>() ;
    interface Callback{
        void onClick(View view,ImgInfo imgInfo);
    }
    private Callback callback;
    ImgListAdapter(Callback callback){this.callback = callback;}
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.img_list_item,viewGroup,false);
        final Holder holder = new Holder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(v,imgInfoList.get(holder.getAdapterPosition()));
            }
        });
        return holder
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView textTag,textcomment;
        ImageView imageView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_list_img);
            textTag = itemView.findViewById(R.id.text_tag);
            textcomment = itemView.findViewById(R.id.text_comment);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return imgInfoList.size();
    }
}
