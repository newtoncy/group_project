package com.newtoncy.group_project.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(lp);
        imageView.setBackgroundColor(0xFF000000);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(onClickListener);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView imageView = (ImageView)holder.itemView;
        imageView.setImageBitmap(m_bitmapList[position]);

    }

    private Bitmap[] m_bitmapList = null;
    private View.OnClickListener onClickListener = null;
    RVAdapter(Bitmap[] bitmapList, View.OnClickListener onClickListener){
        m_bitmapList = bitmapList;
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return m_bitmapList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
