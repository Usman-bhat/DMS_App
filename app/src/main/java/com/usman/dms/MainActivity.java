package com.usman.dms;

import static com.usman.dms.StaticData.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.usman.dms.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        getSupportActionBar().hide();
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        setLangSP(MainActivity.this);
        //add all

        binding = ActivityMainBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        binding.changeLanguage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageDialog();
            }
        } );

        BottomNavigationView navView = findViewById( R.id.nav_view );
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_home,R.id.navigation_notifications )
                .build();
        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment_activity_main );
//        NavigationUI.setupActionBarWithNavController( this, navController, appBarConfiguration );
        NavigationUI.setupWithNavController( binding.navView, navController );
    }

    private void showLanguageDialog() {

        Dialog dialog = new Dialog( this );
        dialog.setContentView( R.layout.language_dialog );
        dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT );
        dialog.setCancelable( true );
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().setBackgroundDrawableResource( android.R.color.transparent );
        Button eng  = dialog.findViewById( R.id.eng );
        Button urd  = dialog.findViewById( R.id.urd );

        urd.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocalLang(MainActivity.this,"ur");
                dialog.dismiss();
                finish();
                startActivity( getIntent() );
            }
        } );
        eng.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocalLang(MainActivity.this,"en");
                dialog.dismiss();
                finish();
                startActivity( getIntent() );
            }
        } );
        dialog.show();

//        String lang[]  = {"English","urdu"};
//        AlertDialog.Builder builder= new AlertDialog.Builder( this );
//        builder.setTitle( "Choose Language" );
//        builder.setSingleChoiceItems( lang, -1, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if(i == 0){
//                    setLocalLang("en");
//                    recreate();
//                }else if(i == 1){
//                    setLocalLang("ur");
//                    recreate();
//                }
//                builder.create();
//                builder.show();
//            }
//        } );
    }

    private void setLocalLang(Activity activity, String lang) {
        Locale locale1 = new Locale( lang );
        Locale.setDefault( locale1 );

        Resources resources = activity.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale1;
        resources.updateConfiguration( configuration,resources.getDisplayMetrics() );

        SharedPreferences.Editor editor = getSharedPreferences( "Settings",MODE_PRIVATE ).edit();
        editor.putString( "app_lang",lang );
        editor.commit();
    }

    void setLangSP(Activity activity){
        SharedPreferences sp = getSharedPreferences( "Settings",MODE_PRIVATE );
        String lFromSp = sp.getString( "app_lang","" );
        setLocalLang( activity,lFromSp );
    }

}