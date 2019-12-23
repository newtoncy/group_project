package com.newtoncy.group_project.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.newtoncy.group_project.R;
import com.newtoncy.group_project.javaclass.ImgInfo;
import com.newtoncy.utils.ServerURL;

import java.util.ArrayList;
import java.util.List;

public class ImgListAdapter extends RecyclerView.Adapter {
    List<ImgInfo> imgInfoList = null ;
    public interface Callback{
        void onClick(View view,ImgInfo imgInfo,int position);
    }
    private Callback callback;
    private Context context;
    public ImgListAdapter(Context context, List<ImgInfo> imgInfoList, Callback callback){
        this.imgInfoList = imgInfoList;
        this.context = context;
        this.callback = callback;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.img_list_item,viewGroup,false);
        final Holder holder = new Holder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(v,imgInfoList.get(holder.getAdapterPosition()),holder.getAdapterPosition());
            }
        });
        return holder;
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
        Holder holder = (Holder) viewHolder;
        ImgInfo imgInfo = imgInfoList.get(i);
        holder.textTag.setText(imgInfo.tag);
        holder.textcomment.setText(imgInfo.comment);
        RoundedCorners roundedCorners= new RoundedCorners(30);
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).centerCrop();
        Glide.with(context).load(ServerURL.getURL(imgInfo.imgPath)).apply(options).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imgInfoList.size();
    }
}
