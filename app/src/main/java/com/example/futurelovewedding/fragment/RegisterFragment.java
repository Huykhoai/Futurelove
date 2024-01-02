package com.example.futurelovewedding.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.futurelovewedding.R;
import com.example.futurelovewedding.api.QueryValueCallBack;
import com.example.futurelovewedding.api.RetrofitClient;
import com.example.futurelovewedding.model.Login;
import com.example.futurelovewedding.server.ApiServer;
import com.example.futurelovewedding.server.Server;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterFragment extends Fragment {
    Button btnRegister;
    TextView btnLogin,tvuser_alert,tvemail_alert,tvpass_alert;
    TextInputLayout til_username,til_email,til_pass;
    TextInputEditText ed_username,ed_email,ed_pass;
    private final String EXISTED_ACCOUNT_STR = "{message=Account already exists!}";
    public RegisterFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Anhxa(view);
        setupTextWatch();
        BtnRegister();
        BtnLogin();
        return view;
    }

    private void BtnLogin() {
        btnLogin.setOnClickListener(v -> {
            navLoginFragment();
        });
    }

    private void BtnRegister(){
        btnRegister.setOnClickListener(v -> {
            String username = ed_username.getText().toString();
            String email = ed_email.getText().toString();
            String pass = ed_pass.getText().toString();
            if(isCompletedInformation(username,email,pass)){
                checkAccountRegister(username,email,pass);
            }
        });
    }
    private void checkAccountRegister(String username,String email,String pass){
        callSigninApi(new QueryValueCallBack() {
            @Override
            public void onQueryValueReceived(String query) {
                if(!query.contains(EXISTED_ACCOUNT_STR)){
                    Log.d("Huy", "onQueryValueReceived: "+query);
                  Dialog dialog = showRegistrationSuccessDialog();
                  new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                      @Override
                      public void run() {
                          dialog.dismiss();
                          navLoginFragment();
                      }
                  },5000);
                }else {
                    Log.d("Huy", "onQueryValueReceived: "+query);
                }
            }

            @Override
            public void onApiCallFailed(Throwable tb) {

            }
        },username,email,pass);
    }
    private void setupTextWatch(){
      ed_username.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
                  userlAlertVisibility();
          }

          @Override
          public void afterTextChanged(Editable s) {

          }
      });
      ed_email.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
           emailAlertVisibility();
          }

          @Override
          public void afterTextChanged(Editable s) {

          }
      });
      ed_pass.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
               passAlertVisibility();
          }

          @Override
          public void afterTextChanged(Editable s) {

          }
      });
    }
    private void emailAlertVisibility(){
        String email = Objects.requireNonNull(ed_email.getText().toString());
        boolean isValidateemail = isValidateEmail(email);
        tvemail_alert.setVisibility(isValidateemail? View.GONE : View.VISIBLE);
    }
    private void userlAlertVisibility(){
        String username = Objects.requireNonNull(ed_username.getText().toString());
        boolean isValidateUser = isValidateEmail(username);
        tvuser_alert.setVisibility(isValidateUser? View.GONE : View.VISIBLE);
    }
    private void passAlertVisibility(){
        String pass = Objects.requireNonNull(ed_pass.getText().toString());
        boolean isValidatePass= isValidatePassword(pass);
        tvpass_alert.setVisibility(isValidatePass? View.GONE : View.VISIBLE);
    }
    private Dialog showRegistrationSuccessDialog(){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_register_success);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        return dialog;
    }
    private boolean isCompletedInformation(String userName, String email, String password) {
        if(isValidateUsername(userName)&& isValidateEmail(email)&&isValidatePassword(password)){
            return true;
        }
        return false;
    }
    private void callSigninApi(QueryValueCallBack callBack,String username,String email,String pass){
        ApiServer apiServer = RetrofitClient.getInstance(Server.URI).getRetrofit().create(ApiServer.class);
        Call<Login> call = apiServer.signup(email,pass,username,"https://i.pinimg.com/564x/3f/94/70/3f9470b34a8e3f526dbdb022f9f19cf7.jpg", String.valueOf(1));
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if(response.isSuccessful()&& response.body()!=null){
                  callBack.onQueryValueReceived(response.body().toString());
                    Log.d("Huy", "body: "+response.body());
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }
    private boolean isValidateEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidatePassword(String pass){
      return pass.length()>=8 && !containSpecialCharacters(pass);
    }
    private boolean isValidateUsername(String username){
        return username.length()>=8 && !containSpecialCharacters(username);
    }
    private boolean containSpecialCharacters(String pass){
        String special = "~`!@#$%^&*()+={}[]|\\:;\"<>,.?/ ";
        for(int i=0;i<pass.length();i++){
            if(special.contains(String.valueOf(pass.charAt(i)))){
                return true;
            }
        }
       return  false;
    }
   private void navLoginFragment(){
       NavHostFragment.findNavController(RegisterFragment.this).navigate(R.id.action_registerFragment_to_loginFragment);
   }
    private void Anhxa(View view) {
        btnLogin =view.findViewById(R.id.txtlogin);
        btnRegister = view.findViewById(R.id.btn_register);
        til_username = view.findViewById(R.id.til_username_register);
        til_email = view.findViewById(R.id.til_email_register);
        til_pass = view.findViewById(R.id.til_pass_register);
        ed_email = view.findViewById(R.id.edit_email_register);
        ed_username = view.findViewById(R.id.edit_user_register);
        ed_pass = view.findViewById(R.id.edit_pass_register);
        tvuser_alert = view.findViewById(R.id.tv_user_name_alert);
        tvemail_alert = view.findViewById(R.id.tv_email_alert);
        tvpass_alert = view.findViewById(R.id.tv_password_alert);
    }
}