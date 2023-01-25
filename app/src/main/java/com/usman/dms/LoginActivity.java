package com.usman.dms;

import static com.usman.dms.StaticData.ACCESS_TOKEN_SP;
import static com.usman.dms.StaticData.LOGINSP;
import static com.usman.dms.StaticData.LOGINURL;
import static com.usman.dms.StaticData.REFRESH_TOKEN_SP;
import static com.usman.dms.StaticData.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.usman.dms.databinding.ActivityLoginBinding;
import com.usman.dms.models.ErrorResponseModel;
import com.usman.dms.models.LoginResponseModel;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    String url = LOGINURL;
    TextView loginErrorTv;
    EditText email,password;
    Button login_btn;
    ActivityLoginBinding activityLoginBinding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        activityLoginBinding = ActivityLoginBinding.inflate( getLayoutInflater());
        View view = activityLoginBinding.getRoot();
        setContentView( view );
        getSupportActionBar().hide();
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        email = activityLoginBinding.email;
        password = activityLoginBinding.password;
        login_btn = activityLoginBinding.loginBtn;
        loginErrorTv = activityLoginBinding.loginErrorTv;

        SharedPreferences sp = getSharedPreferences( LOGINSP,MODE_PRIVATE );
        SharedPreferences.Editor spEditor = sp.edit();




        login_btn.setOnClickListener( new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String emailVal =email.getText().toString().trim();
                String passwordVal =password.getText().toString().trim();
//                Log.e( TAG, "onClick: "+emailVal+":  "+passwordVal );
                if(email.getText()==null || password.getText()==null || emailVal.equals( "" ) || passwordVal.equals( "" )){
                    loginErrorTv.setVisibility( View.VISIBLE );
                    loginErrorTv.setText( "Please Fill the below data" );
                }else {
                    RequestQueue queue = Volley.newRequestQueue( LoginActivity.this );
//                    Log.e( "me", "onCreate: " + emailVal + passwordVal );

                    StringRequest stringRequest = new StringRequest( Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    GsonBuilder gsonBuilder = new GsonBuilder();
                                    Gson gson = gsonBuilder.create();
                                    if (response.contains( ACCESS_TOKEN_SP )) {
                                        LoginResponseModel loginResponse = gson.fromJson( response, LoginResponseModel.class );
                                        spEditor.putString( ACCESS_TOKEN_SP, loginResponse.getAccessToken() );
                                        spEditor.putString( REFRESH_TOKEN_SP, loginResponse.getRefreshToken() );
                                        spEditor.putString( "user", emailVal );
                                        spEditor.commit();
                                        Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                                        startActivity( intent );
//                                    Log.e( "me", "onResponse:  OKK"+loginResponse.getAccessToken());
//                                    Log.e( "me", "onResponse:  OKK"+loginResponse.getRefreshToken());


                                    } else if (response.contains( "error" )) {
                                        loginErrorTv.setVisibility( View.VISIBLE );
                                        loginErrorTv.setText( "Wrong Email OR Password" );
                                    } else {
                                        loginErrorTv.setVisibility( View.VISIBLE );
                                        loginErrorTv.setText( "Some Error Occurred PLease Try again." );
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loginErrorTv.setVisibility( View.VISIBLE );
                            loginErrorTv.setText( "Sorry! Server Error" );
                        }
                    } ) {
                        //This is for Headers If You Needed
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
//                        params.put("token", "ACCESS_TOKEN");
//                        return params;
//                    }
//                    {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put( "email", emailVal );
                            map.put( "password", passwordVal );

                            return map;
                        }
                    };
                    queue.add(stringRequest);

                }
                //end else

            }
        } );
    }
}