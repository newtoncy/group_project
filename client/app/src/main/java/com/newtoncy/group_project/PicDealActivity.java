package com.newtoncy.group_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PicDealActivity extends AppCompatActivity {

    private Bitmap m_orgBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String path = getIntent().getStringExtra("path");
        m_orgBitmap = BitmapFactory.decodeFile(path);
        setContentView(R.layout.activity_pic_deal);
        RecyclerView recyclerView = findViewById(R.id.pic_deal_rv);
        Bitmap[] bitmaps = new Bitmap[6];
        bitmaps[0] = m_orgBitmap;
        PicDeal picDeal = new PicDeal(m_orgBitmap);
        bitmaps[1] = picDeal.bitmap2;
        bitmaps[2] = picDeal.bitmap3;
        bitmaps[3] = picDeal.bitmap4;
        bitmaps[4] = picDeal.bitmap5;
        bitmaps[5] = picDeal.bitmap6;
        RVAdapter rvAdapter = new RVAdapter(bitmaps, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TagActivity.toActivity(PicDealActivity.this,path);
            }
        });
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

    }
    public static void toActivity(Activity activity, String path){
        Intent intent = new Intent(activity, PicDealActivity.class);
        intent.putExtra("path",path);
        activity.startActivity(intent);
    }
}
