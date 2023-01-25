package com.usman.dms;

import static com.usman.dms.StaticData.ACCESS_TOKEN_SP;
import static com.usman.dms.StaticData.LOGINSP;
import static com.usman.dms.StaticData.LOGINURL;
import static com.usman.dms.StaticData.REFRESH_TOKEN_SP;
import static com.usman.dms.StaticData.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
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
import com.usman.dms.models.ErrorResponseModel;
import com.usman.dms.models.LoginResponseModel;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    String url = LOGINURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );

        getSupportActionBar().hide();
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences sp = getSharedPreferences( LOGINSP,MODE_PRIVATE );
        SharedPreferences.Editor spEditor = sp.edit();

        if(!(sp.contains( ACCESS_TOKEN_SP )&& sp.contains( REFRESH_TOKEN_SP ))){
            Log.e( "me", "onCreate: not in sp " );
            new Handler().postDelayed( new Runnable() {
                @Override
                public void run() {
                    Intent i=new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 3000);
        }else{
            String REFRESH_TOKEN = sp.getString( REFRESH_TOKEN_SP,"" );
            Log.e( TAG, "data in sp " );
            RequestQueue queue = Volley.newRequestQueue(SplashActivity.this);
            StringRequest stringRequest = new StringRequest( Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // on Success
                            Log.e( TAG, "onResponse: "+ response);
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();
                            if(response.contains(ACCESS_TOKEN_SP)){
                                LoginResponseModel loginResponse = gson.fromJson( response, LoginResponseModel.class );
                                spEditor.putString( ACCESS_TOKEN_SP, loginResponse.getAccessToken());
                                spEditor.putString( REFRESH_TOKEN_SP, loginResponse.getRefreshToken());
                                spEditor.commit();
                                new Handler().postDelayed( new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                                        startActivity( intent );
                                        finish();
                                    }
                                }, 3000);

                            }else if(response.contains("error") || response.contains( "Bad Request" )){
                                spEditor.remove( ACCESS_TOKEN_SP );
                                spEditor.remove( REFRESH_TOKEN_SP );
                                spEditor.remove( "user" );
                                spEditor.commit();
                                try {
                                    ErrorResponseModel errorResponce = gson.fromJson( response, ErrorResponseModel.class );
                                    Toast.makeText( SplashActivity.this, errorResponce.getMessage()!=null ? errorResponce.getMessage():"Error Occurred", Toast.LENGTH_LONG ).show();

                                }catch (Exception e){
                                    Toast.makeText( SplashActivity.this, "Error!11122", Toast.LENGTH_LONG ).show();
                                }
                                new Handler().postDelayed( new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i=new Intent(SplashActivity.this,LoginActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }, 3000);


                            }else{
                                Log.e( "me", "onResponse:  NOTHING");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e( "me", "onResponse: "+ error);
                    new Handler().postDelayed( new Runnable() {
                        @Override
                        public void run() {
                            Intent i=new Intent(SplashActivity.this,LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }, 3000);

                }
            }){
                    //Headers
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        params.put("authorization", "Bearer "+REFRESH_TOKEN);
                        return params;
                    }


                //POST_PERMS
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params1 = new HashMap<String, String>();
                    params1.put("refresh", "1");
                    return params1;
                }
            };

            queue.add(stringRequest);

        }

    }
}