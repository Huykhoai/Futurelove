package com.example.futurelovewedding.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.futurelovewedding.R;
import com.example.futurelovewedding.api.QueryValueCallBack;
import com.example.futurelovewedding.api.RetrofitClient;
import com.example.futurelovewedding.model.Login;
import com.example.futurelovewedding.server.ApiServer;
import com.example.futurelovewedding.server.Server;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {
    Button btnLogin;
    TextInputEditText ed_email_login,ed_pass_login;
    TextInputLayout til_email_login,til_pass_login;
    CheckBox cb;
    TextView btnRegister;
    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Anhxa(view);
        BtnLogin();
        btnRegister();
        return view;
    }

    private void Anhxa(View view) {
        btnLogin = view.findViewById(R.id.btnLogin);
        ed_email_login = view.findViewById(R.id.edit_email_login);
        ed_pass_login = view.findViewById(R.id.edit_pass_login);
        til_email_login = view.findViewById(R.id.til_email);
        til_pass_login = view.findViewById(R.id.til_pass);
        btnRegister = view.findViewById(R.id.txtregister);
        cb = view.findViewById(R.id.checkBox);
    }

    private void BtnLogin(){
        btnLogin.setOnClickListener(v -> {
            String email = ed_email_login.getText().toString();
            String password = ed_pass_login.getText().toString();
            if(isCompletedInfomation(email,password)){
                checkAccountRegister(email,password);
            }else {
                Toast.makeText(getActivity(), "Thất bại", Toast.LENGTH_SHORT).show();
            }
        });

    }
   private void btnRegister(){
       btnRegister.setOnClickListener(v -> {
           NavController nav = NavHostFragment.findNavController(this);
           nav.navigate(R.id.action_loginFragment_to_resgisterFragment);
       });
   }
    private void checkAccountRegister(String email,String pass){
         callLoginApi(new QueryValueCallBack() {
             @Override
             public void onQueryValueReceived(String query) {
               if(query.contains("Logined in successfully")){
                   Toast.makeText(getActivity(), "Login Thành công", Toast.LENGTH_SHORT).show();
               }else if(query.contains("Invalid in Password!!")){
                   Toast.makeText(getActivity(), "Invalid Email or Password!!", Toast.LENGTH_SHORT).show();
               }
             }

             @Override
             public void onApiCallFailed(Throwable tb) {

             }
         },email,pass);
    }
    private void callLoginApi(QueryValueCallBack callBack, String email,String pass){

        ApiServer apiServer = RetrofitClient.getInstance(Server.URI).getRetrofit().create(ApiServer.class);
        Call<Login> call = apiServer.login(email,pass);
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
            if(response.isSuccessful()&& response.body()!= null){
              String token = response.body().getToken();
                Log.d("Huytoken", "onResponse: "+token);
              String user_id = String.valueOf(response.body().getId_user());
              if(!user_id.equals("0")){
                    callBack.onQueryValueReceived("Logined in successfully");
              }else {
                  callBack.onQueryValueReceived("Invalid in Password!!");
              }
                Log.d("check_login", "onResponse: "+user_id);
                SharedPreferences spf = getActivity().getSharedPreferences("id_user",0);
                SharedPreferences.Editor edit = spf.edit();
                edit.putString("id_user_str", user_id);
                edit.putString("token", token);
                edit.apply();
            }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
    }
    private boolean isCompletedInfomation(String email,String pass){
        if(isValidEmail(email)&& isValidPassword(pass)){
           return true;
        }
        return false;
    }
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password) {
        return password.length() >= 8 ;
    }
}