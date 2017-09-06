package com.zpower.ui.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zpower.R;
import com.zpower.service.DBService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by zx on 2017/2/20.
 */

public class RegisterFragment extends BaseFragment implements View.OnClickListener {

    private ImageView iv_close;
    private TextView tv_to_login;
    private EditText et_email;
    private String email;
    private Button btn_sign_up;
    private EditText et_password;
    private String password;
    private SQLiteDatabase db;
    private View rootView;

    public static RegisterFragment newInstance(){
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register,container,false);
        initView();
        initListener();
        return rootView;
    }

    @Override
    protected FragmentAnimator onCreateFragmentAnimator() {
        // 默认不改变
//         return super.onCreateFragmentAnimation();
        // 在进入和离开时 设定无动画
        return new DefaultNoAnimator();
    }

    private void initView() {
        //iv_close = (ImageView) rootView.findViewById(R.id.iv_close);
        tv_to_login = (TextView) rootView.findViewById(R.id.tv_to_login);
        btn_sign_up = (Button) rootView.findViewById(R.id.btn_sign_up);
        et_email = (EditText) rootView.findViewById(R.id.et_email);
        et_password = (EditText) rootView.findViewById(R.id.et_password);
    }

    private void initListener() {
        tv_to_login.setOnClickListener(this);
        btn_sign_up.setOnClickListener(this);
        //iv_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_to_login:
                start(LoginFragment.newInstance(),SINGLETASK);
                break;
            case R.id.btn_sign_up:
                email = et_email.getText().toString().trim();//用户输入的邮箱
                password = et_password.getText().toString().trim();//用户输入的密码
                // 邮箱验证规则
                String regEx = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
                // 编译正则表达式
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(email);
                //判断邮箱是否已注册
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getActivity(),"邮箱或密码不能为空",Toast.LENGTH_SHORT).show();

                }else if (!matcher.matches()){
                    Toast.makeText(getActivity(),"邮箱格式不正确",Toast.LENGTH_SHORT).show();
                }else {
                    if (DBService.getInstance().checkEmail(email)){
                        if (DBService.getInstance().insertUser(email,password) > 0){
                            Toast.makeText(getActivity(),"注册成功",Toast.LENGTH_SHORT).show();
                            //SPUtils.put(getActivity(),"user_email_signup",email);//保存邮箱到sharedpreferences
                            //SPUtils.put(getActivity(),"user_password",password);
                            start(LoginFragment.newInstance(),SINGLETASK);
                        }
                    }else {
                        Toast.makeText(getActivity(),"邮箱已注册",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
/*            case R.id.iv_close:
                pop();
                break;*/
        }

    }
}
