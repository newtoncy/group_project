package com.example.renkai.login_test;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.newtoncy.utils.Login;
import com.newtoncy.utils.UserProfile;

public class UserInfo2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info2);
        flush();
    }

    @Override
    protected void onResume() {
        super.onResume();
        flush();
    }

    void flush(){
        TextView textUid = findViewById(R.id.text_uid);
        TextView textUserName = findViewById(R.id.text_user_name);
        UserProfile userProfile = Login.getUserProfile();
        textUid.setText(userProfile.uid);
        textUserName.setText(userProfile.userName);
    }
    public static void toActivity(Context context){
        context.startActivity(new Intent(context,UserInfo2Activity.class));
    }

    public void logout(View view) {
        Login.logout();
        Login_Activity.toActivity(this);
    }
}
