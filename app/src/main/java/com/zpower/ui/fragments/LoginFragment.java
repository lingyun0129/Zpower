package com.zpower.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.zpower.utils.SPUtils;

import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private static final String tag = "LoginFragment";

    private View rootView;
    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private ImageView iv_close;
    private TextView tv_to_sign_up;

    /***
     * 初始化LoginFragment
     * @return
     */
    public static LoginFragment newInstance(){
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login,container,false);
        initView();
        return rootView;
    }

    @Override
    protected FragmentAnimator onCreateFragmentAnimator() {
        // 默认不改变
//         return super.onCreateFragmentAnimation();
        // 在进入和离开时 设定无动画
        return new DefaultNoAnimator();
    }

    private void initView(){
        et_email = (EditText) rootView.findViewById(R.id.et_email);
        et_email.setText((CharSequence) SPUtils.get(getActivity(),"user_email",""));//SP中保存的用户邮箱
        et_password = (EditText) rootView.findViewById(R.id.et_password);
        et_password.setText((CharSequence) SPUtils.get(getActivity(),"user_password",""));//SP中的用户密码
        //iv_close = (ImageView) rootView.findViewById(R.id.iv_close);
        btn_login = (Button) rootView.findViewById(R.id.btn_login);
        tv_to_sign_up = (TextView) rootView.findViewById(R.id.tv_to_sign_up);
        btn_login.setOnClickListener(this);
        //iv_close.setOnClickListener(this);
        tv_to_sign_up.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_to_sign_up:
                start(RegisterFragment.newInstance(),SINGLETASK);//SINGLETASK模式启动
                Log.e(tag,"to_sign_up clicked");
                break;
            case R.id.btn_login:
               String email = et_email.getText().toString().trim();//用户输入的邮箱
               String password = et_password.getText().toString().trim();//用户输入的密码
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getActivity(),R.string.empty_warning,Toast.LENGTH_SHORT).show();
                }else {
                    if (DBService.getInstance().checkEmailAndPassword(email,password)){
                            Toast.makeText(getActivity(),R.string.login_success,Toast.LENGTH_SHORT).show();
                            SPUtils.put(getActivity(),"user_email",email);
                            SPUtils.put(getActivity(),"user_password",password);
                        start(OpenBluetoothFragment.newInstance());//跳转到蓝牙扫描界面
                    }else {
                        Toast.makeText(getActivity(),R.string.login_faile,Toast.LENGTH_SHORT).show();
                    }
        }
        break;
  /*          case R.id.iv_close:
                pop();//出栈当前Fragment
                getActivity().finish();
                break;*/
        }
    }
}
