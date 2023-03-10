package com.usman.dms.ui.credit;

import static com.usman.dms.StaticData.CREDITCHARTID;
import static com.usman.dms.StaticData.CREDIT_PIE_CENTER_TEXT;
import static com.usman.dms.StaticData.CREDIT_PIE_LABEL;
import static com.usman.dms.StaticData.GETSETURL;
import static com.usman.dms.StaticData.LOGINSP;
import static com.usman.dms.StaticData.TAG;
import static com.usman.dms.StaticData.YEAR;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.usman.dms.LoginActivity;
import com.usman.dms.R;
import com.usman.dms.SplashActivity;
import com.usman.dms.databinding.FragmentCreditBinding;
import com.usman.dms.inters.CreditRecInterface;
import com.usman.dms.models.CreditModel;
import com.usman.dms.models.PieChartDataModel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreditFragment extends Fragment {

    private FragmentCreditBinding binding;
    private PieChart chart;
    RecyclerView recyclerView;
    CreditAdapter creditAdapter;
    Button addCreditBtn;
    Spinner months_spinner,years_spinner;
    String ACCESS_TOKEN;
    CreditRecInterface creditRecInterface;
    ProgressBar creditRecPbar,creditCreditPbar;
    RequestQueue queue;
    Dialog dialog ;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CreditViewModel dashboardViewModel =
                new ViewModelProvider( this ).get( CreditViewModel.class );
        binding = FragmentCreditBinding.inflate( inflater, container, false );
        View root = binding.getRoot();

        /*###########################        Shared Pref  Stuff Here             ######################################################*/
        SharedPreferences sp = getActivity().getSharedPreferences(LOGINSP, Context.MODE_PRIVATE);
        if(!sp.contains( "accessToken" )){
            startActivity( new Intent(getActivity(), LoginActivity.class ) );
        }else{
            ACCESS_TOKEN = sp.getString( "accessToken",null );
        }

        /*###########################        PieChart Stuff Here   @DONE           ######################################################*/
        chart = binding.creditPieChart;
        creditCreditPbar = binding.creditCreditPbar;
        chart.setVisibility( View.GONE );
        creditCreditPbar.setVisibility( View.VISIBLE );
        setDebitPieChartData(chart,CREDITCHARTID,CREDIT_PIE_CENTER_TEXT,CREDIT_PIE_LABEL,YEAR);

        /*###########################        RecyclerView  Stuff Here             ######################################################*/
        recyclerView = binding.creditRecView;
        creditRecPbar = binding.creditRecPbar;
        months_spinner = binding.monthsSpinner;
        years_spinner = binding.yearsSpinner;
        setCreditRecViewData();

        /*###########################       Dialog+AddCredit  Stuff Here             ######################################################*/
        addCreditBtn  = binding.addCreditBtn;
        addCreditBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCreditData();
            }
        } );
//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe( getViewLifecycleOwner(), textView::setText );
        return root;
    }

    private void addCreditData() {
        dialog = new Dialog( getContext() );
        Button submitbtn, cancelbtn;
        EditText amount,amountBy,recieptNo,recieptBy;
        Spinner forSpinner, modeSpinner;
        ProgressBar credit_pbar;
        dialog.setContentView( R.layout.add_credit_dialog );
        dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT );
        dialog.setCancelable( false );
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().setBackgroundDrawableResource( android.R.color.transparent );


        ArrayAdapter<CharSequence> forAdapter = ArrayAdapter.createFromResource( getContext(),R.array.credit_for, androidx.transition.R.layout.support_simple_spinner_dropdown_item );
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource( getContext(),R.array.credit_mode, com.airbnb.lottie.R.layout.support_simple_spinner_dropdown_item );

        forAdapter.setDropDownViewResource( androidx.appcompat.R.layout.support_simple_spinner_dropdown_item );
        modeAdapter.setDropDownViewResource( androidx.appcompat.R.layout.support_simple_spinner_dropdown_item );
        //                finding EditText Views
        amount = dialog.findViewById( R.id.credit_amount );
        amountBy = dialog.findViewById( R.id.credit_amount_by );
        recieptBy = dialog.findViewById( R.id.credit_reciept_by );
        recieptNo = dialog.findViewById( R.id.credit_reciept_no );
        //setting Spinners
        forSpinner = dialog.findViewById( R.id.credit_for_spinner );
        modeSpinner = dialog.findViewById( R.id.credit_mode_spinner );
        credit_pbar = dialog.findViewById( R.id.credit_pbar );

        forSpinner.setAdapter( forAdapter );
        modeSpinner.setAdapter( modeAdapter );


//                setting Button Click
        submitbtn = dialog.findViewById( R.id.add_credit );
        cancelbtn = dialog.findViewById( R.id.cancel_credit );
        submitbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount1 = amount.getText().toString().trim();
                String amountBy1 = amountBy.getText().toString().trim();
                String recieptBy1 = recieptBy.getText().toString().trim();
                String recieptNo1 = recieptNo.getText().toString().trim();
                String for1 = forSpinner.getSelectedItem().toString();
                String mode1 = modeSpinner.getSelectedItem().toString();
                if(amount1.isEmpty() || amountBy1.isEmpty() || recieptBy1.isEmpty() ||
                        recieptNo1.isEmpty() || for1.isEmpty() || mode1.isEmpty() || amount1.matches( "[a-zA-Z]+" )){
                    Toast.makeText( getContext(), "wrong details", Toast.LENGTH_LONG ).show();
                }else{
                    submitbtn.setVisibility( View.INVISIBLE );
                    credit_pbar.setVisibility( View.VISIBLE );
                    sendCreditDataRequest(amount1,amountBy1,recieptNo1,recieptBy1,for1,mode1);
                }
//                        1258,sel,12,ser,Food,Cash
                Log.e( TAG,"Data : "+amount1+","+amountBy1+","+recieptNo1+","+recieptBy1+","+for1+","+mode1);
                dialog.cancel();
            }
        } );
        cancelbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                Toast.makeText( getContext(),"Cancled ", Toast.LENGTH_SHORT ).show();

            }
        } );
        dialog.show();
    }

    private void setCreditRecViewData() {
        //year spinner
        ArrayAdapter<CharSequence> yearSpinnerAdapter = ArrayAdapter.createFromResource( getContext(),R.array.years, android.R.layout.simple_spinner_item );
        yearSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        years_spinner.setAdapter( yearSpinnerAdapter );
        //month spinnner
        ArrayAdapter<CharSequence> monthSpinnerAdapter = ArrayAdapter.createFromResource( getContext(),R.array.months, android.R.layout.simple_spinner_item );
        monthSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        months_spinner.setAdapter( monthSpinnerAdapter );
        creditRecPbar.setVisibility( View.VISIBLE );

        //year spinner change
        years_spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setDebitPieChartData(chart,CREDITCHARTID,CREDIT_PIE_CENTER_TEXT,CREDIT_PIE_LABEL,years_spinner.getSelectedItem().toString());
                creditRecPbar.setVisibility( View.GONE );
                getCreditDataRequest(years_spinner.getSelectedItem().toString(),months_spinner.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                int month = new Date().getMonth();
                creditRecPbar.setVisibility( View.GONE );
                getCreditDataRequest( YEAR,String.valueOf( month ) );
            }
        } );
        //Month Spinner Change
        months_spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                creditRecPbar.setVisibility( View.GONE );
                getCreditDataRequest(years_spinner.getSelectedItem().toString(),months_spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                int month = new Date().getMonth();
                getCreditDataRequest( YEAR,String.valueOf( month ) );
                creditRecPbar.setVisibility( View.GONE );
            }
        } );


    }

    /**
     * Fetch Data from Server And Set it to Chart View
     * @param chart view
     * @param tableId 0=CreditTable 1=Debittable
     * @param centerText text to set in center of the PieChart
     * @param label to set label of the chart
     */

    private void setDebitPieChartData(PieChart chart,int tableId,String centerText,String label,String year) {
        ArrayList<PieEntry> pieChartData = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue( getContext() );
        StringRequest request = new StringRequest( Request.Method.POST, GETSETURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e( TAG, "onResponse: "+response );
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                if(response.contains( "Expired" ) || response.contains( "Token" ) || response.contains( "Invalid" )){
                    Toast.makeText( getActivity(), "Token Expired Refreshing...", Toast.LENGTH_LONG ).show();
                    startActivity( new Intent(getActivity(),SplashActivity.class) );
                    getActivity().finish();
                }else if(response.contains( "error" ) || response.contains( "Error" )) {

                    Toast.makeText( getActivity(), "Error", Toast.LENGTH_LONG ).show();
                }else if(response.contains( "amount" )){
                    chart.setVisibility( View.VISIBLE );
                    creditCreditPbar.setVisibility( View.GONE );
                    /**
                     * SuccessFully Retreved Data from server and now biding it with chart view
                     */
                    PieChartDataModel[] data = gson.fromJson( response,PieChartDataModel[].class );
                    for (PieChartDataModel element :data) {
                        pieChartData.add( new PieEntry(element.getAmount(),String.valueOf( element.getYear() ) ) );
                    }
                    PieDataSet pieDataSet = new PieDataSet( pieChartData,label );
                    pieDataSet.setColors( ColorTemplate.COLORFUL_COLORS );
                    pieDataSet.setValueTextColor( Color.BLACK );
                    pieDataSet.setValueTextSize( 16f );
                    PieData pieData = new PieData(pieDataSet);
                    chart.setData( pieData );
                    //setting dcesc
                    Description description = chart.getDescription();
                    description.setText( getResources().getString( R.string.creditchartdesc ) );
                    chart.setCenterText( centerText );
                    chart.animate();

                }else if(response.contains( "[]" )){
                    Toast.makeText( getActivity(), getResources().getString( R.string.nodatafound ), Toast.LENGTH_LONG ).show();
                }else{
                    Toast.makeText( getActivity(), getResources().getString( R.string.unknownerror ), Toast.LENGTH_LONG ).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText( getActivity(), getResources().getString( R.string.error500 ), Toast.LENGTH_LONG ).show();
                Log.e( "me", error.toString());
            }
        } ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                params.put("authorization", "Bearer "+ACCESS_TOKEN);
                return params;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("action","getPieChatData" );
                map.put("year",year!=null?year:YEAR);
                map.put("table",tableId==CREDITCHARTID?"cr":"dt" );
                return map;
            }
        };
        queue.add( request );

    }

    /**
     * to Save reciept Dialog As Image In Gallery And To Share It
     * @param linearLayout  which we use as Wrapper Of Image
     * @param PrintOrShare   true=print & flase=share Booolean value to check if print btn or share btn is clicked
     * @param filename  String which is used as Name of the Image file
     */
    private void saveReciept(LinearLayout linearLayout,boolean PrintOrShare,String filename) {
        linearLayout.setDrawingCacheEnabled( true );
        linearLayout.buildDrawingCache();
        linearLayout.setDrawingCacheQuality( View.DRAWING_CACHE_QUALITY_HIGH );
        Bitmap bitmap = linearLayout.getDrawingCache();
        if(PrintOrShare){
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File( root+"/Download" );
            File myFile = new File( file,filename );
            if(myFile.exists()){
                myFile.delete();
            }
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream( myFile );
                bitmap.compress( Bitmap.CompressFormat.PNG,100,fileOutputStream );
                fileOutputStream.flush();
                fileOutputStream.close();
                Toast.makeText( getContext(), "Saved SuccessFully", Toast.LENGTH_LONG ).show();
                linearLayout.setDrawingCacheEnabled( false );

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText( getContext(), "Error while Saving Image", Toast.LENGTH_LONG ).show();
            }


        }else{
            try {
                String mapPath = MediaStore.Images.Media.insertImage( getActivity().getContentResolver(),bitmap,"Reciept share","Sharing reciept with user " );
                Uri uri = Uri.parse( mapPath );

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType( "image/*" );
                intent.putExtra( Intent.EXTRA_STREAM,uri );
                startActivity( Intent.createChooser( intent,"Share Image" ) );
                Toast.makeText( getContext(), "Sharing", Toast.LENGTH_SHORT ).show();

            }catch (Exception e){
                Toast.makeText( getContext(), "Error", Toast.LENGTH_SHORT ).show();
            }

        }

    }

    public void getCreditDataRequest(String yearFromSpinner,String monthFromSpinner)
    {


        RequestQueue queue = Volley.newRequestQueue( getContext() );
        StringRequest request = new StringRequest( Request.Method.POST, GETSETURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                if(response.contains( "Expired" ) || response.contains( "Token" ) || response.contains( "Invalid" )){
                    Toast.makeText( getContext(), "Token Expired Refreshing...", Toast.LENGTH_LONG ).show();
                    startActivity( new Intent(getActivity(),SplashActivity.class) );
                    getActivity().finish();
                }else if(response.contains( "error" ) || response.contains( "Error" )) {
                    Toast.makeText( getContext(), getResources().getString( R.string.error500 ), Toast.LENGTH_LONG ).show();
                }else if(response.contains( "cr_id" )){
                    CreditModel[] data = gson.fromJson( response,CreditModel[].class );
                    recyclerView.setLayoutManager(new LinearLayoutManager( getContext() ) );
                    creditAdapter = new CreditAdapter( data, getActivity() ,creditRecInterface);
                    recyclerView.setAdapter( creditAdapter );

                }else if(response.contains( "[]" )){
                    Toast.makeText( getContext(), getResources().getString( R.string.nodatafound ), Toast.LENGTH_LONG ).show();
                }else{
                    Toast.makeText( getContext(), getResources().getString( R.string.unknownerror ), Toast.LENGTH_LONG ).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText( getContext(), getResources().getString( R.string.error500 ), Toast.LENGTH_LONG ).show();
                Log.e( "me", error.toString());
            }
        } ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                params.put("authorization", "Bearer "+ACCESS_TOKEN);
                return params;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("action","getCreditData" );

                map.put("year",yearFromSpinner );

                map.put("month",monthFromSpinner.isEmpty() ? null : monthFromSpinner );
                return map;
            }
        };
        queue.add( request );
    }

    private void sendCreditDataRequest(String amount, String by, String recNo, String recBy, String for1, String mode)
    {

        RequestQueue queue =Volley.newRequestQueue( getContext() );
        StringRequest request = new StringRequest( Request.Method.POST, GETSETURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                if(response.contains( "Inserted Successfully" )){
                    Toast.makeText( getContext(), getResources().getString( R.string.insertedsuccessfully ), Toast.LENGTH_LONG ).show();
                    dialog.dismiss();
                    setCreditRecViewData();
                }else if(response.contains( "Expired" ) || response.contains( "Token" ) || response.contains( "Invalid" )){
                    Toast.makeText( getActivity(), "Token Expired Refreshing...", Toast.LENGTH_LONG ).show();
                    startActivity( new Intent(getActivity(),SplashActivity.class) );
                    getActivity().finish();
                }else {
                    Toast.makeText( getContext(), getResources().getString( R.string.error500 ), Toast.LENGTH_LONG ).show();
                }
                Log.e( "me", response );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText( getContext(), getResources().getString( R.string.error500 ), Toast.LENGTH_LONG ).show();
                Log.e( "me", error.toString());
            }
        } ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                params.put("authorization", "Bearer "+ACCESS_TOKEN);
                return params;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("action","setCreditData" );

                map.put("amount",amount );
                map.put("by",by );
                map.put("rec_no",recNo );
                map.put("rec_by",recBy );
                map.put("mode",mode );
                map.put("for",for1 );

//                action=setCreditData&amount=125&by=me&for=food&rec_no=5&rec_by=red&mode=cash

                return map;
            }
        };
        queue.add( request );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}