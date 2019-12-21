package com.example.renkai.login_test;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.newtoncy.utils.Login;
import com.newtoncy.utils.RequestFailImp;
import com.newtoncy.utils.UserProfile;

import okhttp3.Response;

/**

 */

public class Register_Activity extends Activity implements View.OnClickListener {


    private EditText edit_register, edit_setpassword, edit_resetpassword,edit_username;
    private Button btn_yes, btn_cancel;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        init();
        dbHelper = new DBHelper(this, "Data.db", null, 1);
    }


    protected void init() {
        edit_register = (EditText) findViewById(R.id.edit_register);
        edit_register.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if (!Character.isLetterOrDigit(source.charAt(i)) &&
                                    !Character.toString(source.charAt(i)).equals("_")) {
                                Toast.makeText(Register_Activity.this, "只能使用'_'、字母、数字、汉字注册！", Toast.LENGTH_SHORT).show();
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });
        edit_register.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_register.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_register.getWindowToken(), 0);
                }
                return false;
            }
        });
        edit_setpassword = (EditText) findViewById(R.id.edit_setpassword);
        edit_setpassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String s = v.getText().toString();
                    //设置密码长度有问题，判断editText的输入长度需要重新理解
                    System.out.println(" v: ****** v :"+ s.length());
                    if (s.length() >= 6) {
                        System.out.println(" ****** s :"+ s.length());
                        edit_setpassword.clearFocus();
                        InputMethodManager imm =
                                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edit_setpassword.getWindowToken(), 0);
                    } else {
                        Toast.makeText(Register_Activity.this, "密码设置最少为6位！", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        edit_resetpassword = (EditText) findViewById(R.id.edit_resetpassword);
        edit_resetpassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_resetpassword.clearFocus();
                    InputMethodManager im =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(edit_resetpassword.getWindowToken(), 0);
                }
                return false;
            }
        });
        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(this);
        btn_cancel = (Button) findViewById(R.id.btn_cancle);
        btn_cancel.setOnClickListener(this);
        edit_username = findViewById(R.id.edit_username);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_yes) {
            if (edit_setpassword.getText().toString().trim().
                    equals(edit_resetpassword.getText().toString())) {

                regist(edit_register.getText().toString(), edit_setpassword.getText().toString(), edit_username.getText().toString());

            } else {
                Toast.makeText(this, "两次输入密码不同，请重新输入！",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btn_cancle) {
            this.finish();
        }
    }

    private void regist(String uid, String password, String userName) {
        btn_yes.setEnabled(false);
        Login login = new Login(new RequestFailImp(this){
            @Override
            public void onFail(Response response, int reason, Object e) {
                super.onFail(response, reason, e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_yes.setEnabled(true);
                    }
                });
            }
        });
        login.signUp(new UserProfile(uid, userName, password), new Login.Callback() {
            @Override
            public void success(final UserProfile userProfile) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userProfile.savePwd(Register_Activity.this);
                        Toast.makeText(Register_Activity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                        Register_Activity.this.finish();
                    }
                });
            }
        });
    }


    /**
     * 利用sql创建嵌入式数据库进行注册访问
     */
    private void registerUserInfo(String username, String userpassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", userpassword);
        db.insert("usertable", null, values);
        db.close();
    }



}
