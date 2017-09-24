package ibeacondata.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.example.accelerometersensortest.R;

/**
 * Created by lenovo on 2016/10/31.
 */
public class LoginActivity extends Activity {
    AutoCompleteTextView act_autoet;
    EditText psd_et;
    Button login_button;
    CheckBox psd_save_cb;
    SharedPreferences sp_upload;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        act_autoet = (AutoCompleteTextView) findViewById(R.id.tv_act);
        psd_et = (EditText) findViewById(R.id.tv_psd);
        login_button = (Button) findViewById(R.id.bt_login);
        psd_save_cb = (CheckBox) findViewById(R.id.save_psd_cb);
        psd_save_cb.setChecked(true);
        sp_upload = getSharedPreferences("upload", MODE_PRIVATE);
        act_autoet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String[] allUserName = new String[sp_upload.getAll().size()];// sp.getAll().size()返回的是有多少个键值对
                allUserName = sp_upload.getAll().keySet().toArray(new String[0]);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        allUserName);

                act_autoet.setAdapter(adapter);// 设置数据适配器
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                psd_et.setText(sp_upload.getString(act_autoet.getText()
                        .toString(), ""));
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View view) {
                String id = act_autoet.getText().toString();
                String psw = psd_et.getText().toString();
                if (id.equals("") || psw.equals("")){
                    Toast.makeText(LoginActivity.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                }else if (!id.equals("zhanhui") || !psw.equals("11111")){
                    Toast.makeText(LoginActivity.this, "用户名错误", Toast.LENGTH_SHORT).show();
                } else if (id.equals("zhanhui") && psw.equals("11111")){
                    if (psd_save_cb.isChecked()){
                        sp_upload.edit().putString(act_autoet.getText()
                            .toString(), psd_et.getText().toString()).apply();
                    }
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    act_autoet.setText("");
                    psd_et.setText("");
                }
            }
        });
    }
}