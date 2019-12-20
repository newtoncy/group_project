package com.example.renkai.login_test;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.newtoncy.utils.Login;
import com.newtoncy.utils.RequestFail;
import com.newtoncy.utils.RequestFailImp;
import com.newtoncy.utils.UserProfile;

import okhttp3.Response;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_account, edit_password;
    private TextView text_msg;
    private Button btn_login, btn_register;
    private ImageButton openpwd;
    private boolean flag = false;
    private String account, password;
    private DBHelper dbHelper;

    public static void toActivity(Context context){
        context.startActivity(new Intent(context,Login_Activity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        getSupportActionBar().hide();
        init();

    }



    @Override
    protected void onResume() {
        super.onResume();
        if(Login.isLogin()){
            this.finish();
        }
    }

    private void init() {
        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_account.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_account.clearFocus();
                }
                return false;
            }
        });
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_password.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_password.getWindowToken(), 0);
                }
                return false;
            }
        });
        text_msg = (TextView) findViewById(R.id.text_msg);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        openpwd = (ImageButton) findViewById(R.id.btn_openpwd);
        text_msg.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        openpwd.setOnClickListener(this);
        dbHelper = new DBHelper(this, "Data.db", null, 1);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {
            if (edit_account.getText().toString().trim().equals("") | edit_password.getText().
                    toString().trim().equals("")) {
                Toast.makeText(this, "请输入账号或者注册账号！", Toast.LENGTH_SHORT).show();
            } else {
                login(edit_account.getText().toString(), edit_password.getText().toString());
            }
        } else if (id == R.id.btn_register) {
            Intent intent = new Intent(Login_Activity.this, Register_Activity.class);
            startActivity(intent);
        } else if (id == R.id.btn_openpwd) {
            if (flag == true) {//不可见
                edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                flag = false;
                openpwd.setBackgroundResource(R.drawable.invisible);
            } else {
                edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                flag = true;
                openpwd.setBackgroundResource(R.drawable.visible);
            }
        } else if (id == R.id.text_msg) {
            Intent i = new Intent(Login_Activity.this, ForgotInfo_activity.class);
            startActivity(i);
        }
    }

    private void login(String uid, String password) {
        btn_login.setEnabled(false);
        RequestFail requestFail = new RequestFailImp(this){
            @Override
            public void onFail(Response response, int reason, Object e) {
                super.onFail(response, reason, e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_login.setEnabled(true);
                    }
                });
            }
        };
        new Login(requestFail).login(uid,password, new Login.Callback(){
            @Override
            public void success(final UserProfile userProfile) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userProfile.savePwd(Login_Activity.this);
                        Toast.makeText(Login_Activity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                        Login_Activity.this.finish();
                    }
                });

            }
        });
    }


}
