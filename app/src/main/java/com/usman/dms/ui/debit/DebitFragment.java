package com.usman.dms.ui.debit;

import static com.usman.dms.StaticData.CREDITCHARTID;
import static com.usman.dms.StaticData.DEBITCHARTID;
import static com.usman.dms.StaticData.DEBIT_PIE_CENTER_TEXT;
import static com.usman.dms.StaticData.DEBIT_PIE_LABEL;
import static com.usman.dms.StaticData.GETSETURL;
import static com.usman.dms.StaticData.LOGINSP;
import static com.usman.dms.StaticData.NAME;
import static com.usman.dms.StaticData.TAG;
import static com.usman.dms.StaticData.YEAR;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.os.Environment;
import android.service.quicksettings.Tile;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.usman.dms.databinding.FragmentDebitBinding;
import com.usman.dms.models.DebitModel;
import com.usman.dms.models.PieChartDataModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DebitFragment extends Fragment {

    private FragmentDebitBinding binding;
    PieChart debitPieChart;
    String ACCESS_TOKEN;
    RecyclerView debitRecView;
    Button addDebitBtn,generateDebitPdfBtn;
    DebitAdapter debitAdapter;
    Spinner months_spinner,yearSpinner;
    Dialog dialog;
    ProgressBar debitDebitPbar;
    DebitModel[] fullData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DebitViewModel notificationsViewModel =
                new ViewModelProvider( this ).get( DebitViewModel.class );

        binding = FragmentDebitBinding.inflate( inflater, container, false );
        View root = binding.getRoot();


/*###########################        Shared Pref  Stuff Here             ######################################################*/
        SharedPreferences sp = getActivity().getSharedPreferences(LOGINSP, Context.MODE_PRIVATE);
        if(!sp.contains( "accessToken" )){
            startActivity( new Intent(getActivity(), LoginActivity.class ) );
        }else{
            ACCESS_TOKEN = sp.getString( "accessToken",null );
        }


/*###########################        PieChart Stuff Here   @DONE           ######################################################*/
        debitPieChart = binding.debitPieChart;
        debitDebitPbar=binding.debitDebitPbar;
        debitDebitPbar.setVisibility( View.VISIBLE );
        debitPieChart.setVisibility( View.GONE );
        setDebitPieChartData(debitPieChart,DEBITCHARTID,DEBIT_PIE_CENTER_TEXT,DEBIT_PIE_LABEL,YEAR);

/*###########################        RecyclerView  Stuff Here             ######################################################*/
        debitRecView = binding.debitRecView;
        months_spinner = binding.monthsSpinner;
        yearSpinner = binding.yearSpinner;
        setDebitRecViewData();
/*###########################       Dialog+AddDebit  Stuff Here             ######################################################*/
        addDebitBtn= binding.addDebitBtn;
        addDebitBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDebitData();
            }
        } );


/*###########################        Generate PDF   Stuff Here             ######################################################*/
        generateDebitPdfBtn = binding.generateDebitPdfBtn;
        generateDebitPdfBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateDebitPdf();
            }
        } );





//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe( getViewLifecycleOwner(), textView::setText );
        return root;
    }


    /**
     * @implNote Dialog
     * Show Dialog for Generating Debit PDF
     */
    private void generateDebitPdf() {
        Dialog dialog = new Dialog( getContext() );
        Spinner years , monthes;
        Button generateBtn,cancelBtn;
        dialog.setContentView( R.layout.generate_pdf );
        dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT );
        dialog.setCancelable( false );
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().setBackgroundDrawableResource( android.R.color.transparent );


        generateBtn = dialog.findViewById( R.id.generate_btn );
        cancelBtn = dialog.findViewById( R.id.cancel_btn );
        generateBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    generatePDFfrmData(fullData);
                    Toast.makeText( getContext(), "Pdf Saved Successfully...", Toast.LENGTH_SHORT ).show();
                } catch (Exception e) {
                    Toast.makeText( getContext(), "Cannot Generate Pdf", Toast.LENGTH_SHORT ).show();
                    e.printStackTrace();
                }

            }
        } );
        cancelBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( getContext(), "Cancled", Toast.LENGTH_SHORT ).show();
                dialog.dismiss();
            }
        } );
        dialog.show();


    }

    /**
     * generate pdf from model Class data
     * @param fullData data of type model
     */
    private void generatePDFfrmData(DebitModel[] fullData) throws Exception {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        final int width = 792;
        final int height = 1120;
        PdfDocument pdfDocument = new PdfDocument();
        Paint body = new Paint();
        Paint title = new Paint();
        Paint footer = new Paint();
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(792, 1120, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(mypageInfo);
        Canvas c = page.getCanvas();


        //title
        title.setTypeface( Typeface.createFromAsset( getActivity().getAssets(), "fonts/noorikshda.ttf") );
        title.setTextSize( 40 );
        title.setTextAlign( Paint.Align.CENTER);
        title.setColor( Color.GREEN );
        c.drawText( getResources().getString( R.string.pdf_header ), width/2,50,title );

        //body
        body.setTextSize( 30 );
        body.setTypeface( Typeface.createFromAsset( getActivity().getAssets(), "fonts/noorikshda.ttf") );
        body.setTextAlign( Paint.Align.LEFT );
        body.setColor( Color.DKGRAY );

        c.drawText( getResources().getString( R.string.date )+" -------------- "+getResources().getString( R.string.amount )+" ---------- "+getResources().getString( R.string.debited_by )+" -------- "+getResources().getString( R.string.debited_for ),20,90,body );
        c.drawLine( 20,95,width-30,98,body );

        //from here
        String text = "";
        int sum=0;
        int y = 130;
        for (DebitModel data :fullData) {
            String[] date1 = data.getDt_date().split( " " );
                text += date1[0];
                text += "--------";
                text += data.getDt_amount();
                text += "--------";
                text += data.getDt_by();
                text += "--------";
                text += data.getDt_for();
            c.drawText( text,20,y,body );
            sum+=data.getDt_amount();
            y+=50;
            text="";

        }

        body.setTextSize( 40 );
        c.drawText( "------------------------------------------- TOTAL = "+sum, width/2,50,body );

        //to here
        //footer
        footer.setTextSize( 20 );
        footer.setTypeface( Typeface.createFromAsset( getActivity().getAssets(), "fonts/noorikshda.ttf") );
        footer.setTextAlign( Paint.Align.RIGHT );
        footer.setColor( Color.BLUE );
        c.drawText( getResources().getString( R.string.pdf_footer ),width-20,height-20,footer);





        pdfDocument.finishPage( page );

        String pdfPath = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS ).toString();
        File file = new File( pdfPath,NAME+"DebitRecord"+new Date().getTime()+".pdf" );

        pdfDocument.writeTo( new FileOutputStream( file ) );
        pdfDocument.close();




    }



    /**
     * @implNote Dialog
     * First Show Dialog for Requesting Data
     * and the send that data to server
     */
    private void addDebitData() {
        dialog=new Dialog( getContext() );
        Button submitbtn, cancelbtn;
        EditText amount,debitBy;
        LinearLayout debitError;
        Spinner forSpinner;
        ProgressBar credit_pbar;
        dialog.setContentView( R.layout.add_debit_dialog );
        dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT );
        dialog.setCancelable( false );
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().setBackgroundDrawableResource( android.R.color.transparent );


        ArrayAdapter<CharSequence> forAdapter = ArrayAdapter.createFromResource( getContext(),R.array.credit_for, androidx.transition.R.layout.support_simple_spinner_dropdown_item );

        forAdapter.setDropDownViewResource( androidx.appcompat.R.layout.support_simple_spinner_dropdown_item );

         // finding EditText Views
        amount = dialog.findViewById( R.id.debit_amount );
        debitBy = dialog.findViewById( R.id.debited_by );
        debitError = dialog.findViewById( R.id.debit_error );

        //setting Spinners
        forSpinner = dialog.findViewById( R.id.debit_for_spinner );
        credit_pbar = dialog.findViewById( R.id.debit_pbar );

        forSpinner.setAdapter( forAdapter );
        //setting Button Click
        submitbtn = dialog.findViewById( R.id.add_debit );
        cancelbtn = dialog.findViewById( R.id.cancel_debit );
        submitbtn.setOnClickListener( new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String amount1 = amount.getText().toString().trim();
                String debitedBy1 = debitBy.getText().toString().trim();
                String for1 = forSpinner.getSelectedItem().toString();
                if(amount1.isEmpty() || debitedBy1.isEmpty() || for1.isEmpty()  || amount1.matches( "[a-zA-Z]+" )){
                    debitError.setVisibility( View.VISIBLE );
//                    debitError.getChildAt( 1 ).
                    Toast.makeText( getContext(), "wrong details", Toast.LENGTH_LONG ).show();
                }else{
                    submitbtn.setVisibility( View.INVISIBLE );
                    credit_pbar.setVisibility( View.VISIBLE );
                    sendDebitDataRequest(amount1,debitedBy1,for1);
                }
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



    /**
     * function To set Data To recycler View And Other Stuff Etc.
     * E.g, Sending request To server Etc
     */
    private void setDebitRecViewData() {

        /**  spinner for year*/
        ArrayAdapter<CharSequence> yearSpinnerAdapter = ArrayAdapter.createFromResource( getContext(), R.array.years, android.R.layout.simple_spinner_item );

        yearSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        yearSpinner.setAdapter( yearSpinnerAdapter );

        /**  spinner for month*/
        ArrayAdapter<CharSequence> monthSpinnerAdapter = ArrayAdapter.createFromResource( getContext(), R.array.months, android.R.layout.simple_spinner_item );
        monthSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        months_spinner.setAdapter( monthSpinnerAdapter );
        /**   onclick yearSpinner*/

        months_spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getCreditDataRequest( String.valueOf( yearSpinner.getSelectedItem() ), String.valueOf( months_spinner.getSelectedItem() ) );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                int month =  Calendar.getInstance().get( Calendar.MONTH );
                getCreditDataRequest( "2023",String.valueOf( month ) );
            }
        } );
        /**   onclick yearSpinner*/
        yearSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setDebitPieChartData(debitPieChart,DEBITCHARTID,DEBIT_PIE_CENTER_TEXT,DEBIT_PIE_LABEL,String.valueOf( yearSpinner.getSelectedItem()));
                getCreditDataRequest( String.valueOf( yearSpinner.getSelectedItem() ), String.valueOf( months_spinner.getSelectedItem() ) );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                int month =  Calendar.getInstance().get( Calendar.MONTH );
                getCreditDataRequest( YEAR,String.valueOf( month ) );

            }
        } );
    }


    /**
     * Pie Chart Showes details of amount Debited ;
     * @param chart   PieChart View so that we can set Its data as we want

    private void setDebitPieChartData(PieChart chart) {

        ArrayList<PieEntry> data = new ArrayList<>();
        data.add( new PieEntry( 10000,"Jan" ) );
        data.add( new PieEntry( 15000,"Fab" ) );
        data.add( new PieEntry( 4000,"March" ) );
        data.add( new PieEntry( 20000,"April" ) );
        data.add( new PieEntry( 25000,"May" ) );
        data.add( new PieEntry( 10000,"June" ) );
        data.add( new PieEntry( 8500,"July" ) );
        data.add( new PieEntry( 6500,"August" ) );
        data.add( new PieEntry( 15000,"September" ) );
        data.add( new PieEntry( 7500,"October" ) );
        data.add( new PieEntry( 11000,"November" ) );
        data.add( new PieEntry( 10000,"December" ) );

        PieDataSet pieDataSet = new PieDataSet( data,"Debit Data" );
        pieDataSet.setColors( ColorTemplate.COLORFUL_COLORS );
        pieDataSet.setValueTextColor( Color.BLACK );
        pieDataSet.setValueTextSize( 16f );
        PieData pieData = new PieData(pieDataSet);

        chart.setData( pieData );
        chart.getDescription().setEnabled( false );
        chart.setCenterText( "Amount Spent" );
        chart.animate();
    }
     */
    /**
     * <h1>{@link Volley} Request</h1>
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
                Log.e( TAG, "onResponse: "+response );
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
                    debitDebitPbar.setVisibility( View.GONE );
                    /**
                     * SuccessFully Retreved Data from server and now biding it with chart view
                     */
                    PieChartDataModel[] data = gson.fromJson( response,PieChartDataModel[].class );
                    for (PieChartDataModel element :data) {
                        pieChartData.add( new PieEntry(element.getAmount(),element.getYear()) );
                    }
                    PieDataSet pieDataSet = new PieDataSet( pieChartData,label );
                    pieDataSet.setColors( ColorTemplate.COLORFUL_COLORS );
                    pieDataSet.setValueTextColor( Color.BLACK );
                    pieDataSet.setValueTextSize( 16f );
                    PieData pieData = new PieData(pieDataSet);
                    chart.setData( pieData );
                    //setting desc
                    Description description = chart.getDescription();
                    description.setText( getResources().getString( R.string.debitchartdesc ) );
                    chart.setCenterText( centerText );
                    chart.animate();

                }else if(response.contains( "[]" )){
                    Toast.makeText( getActivity(), getResources().getString( R.string.nodatafound ), Toast.LENGTH_LONG ).show();
                }else{
                    Toast.makeText( getActivity(),getResources().getString( R.string.unknownerror ) , Toast.LENGTH_LONG ).show();
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
     *<h2>Volley: {@link StringRequest}</h2>
     * Send Request To server In order to save debit data in database
     * @param amount1  String amount Spent.
     * @param debitedBy1 String ,Person who Spent amount.
     * @param for1 String, For what purpose he spent it.
     */
    private void sendDebitDataRequest(String amount1, String debitedBy1, String for1) {
        RequestQueue queue = Volley.newRequestQueue( getContext() );

        StringRequest request = new StringRequest( Request.Method.POST, GETSETURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                if(response.contains( "Inserted Successfully" )){
                    Toast.makeText
                            ( getActivity(), getResources().getString( R.string.insertedsuccessfully ), Toast.LENGTH_LONG ).show();
                    dialog.dismiss();
                    setDebitRecViewData();
                }else if(response.contains( "Expired" ) || response.contains( "Token" ) || response.contains( "Invalid" )){
                    Toast.makeText( getActivity(), "Token Expired Refreshing...", Toast.LENGTH_LONG ).show();
                    startActivity( new Intent(getActivity(),SplashActivity.class) );
                    getActivity().finish();
                }else {
                    Toast.makeText( getActivity(), getResources().getString( R.string.unknownerror ), Toast.LENGTH_LONG ).show();
                }
                Log.e( "me", response );
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
                map.put("action","setDebitData" );

                map.put("amount",amount1 );
                map.put("by",debitedBy1 );
                map.put("for",for1 );

                return map;
            }
        };
        queue.add( request );
    }
    /**
     *<h2>Volley: {@link StringRequest}</h2>
     * Send Request To server In order to get data and then set it to Recview
     * @param monthFromSpinner String int from spinner to get month number of which we want to get data.
     */
    private void getCreditDataRequest(String yearFromSpinner,String monthFromSpinner) {
        RequestQueue queue = Volley.newRequestQueue( getContext() );

        StringRequest request = new StringRequest( Request.Method.POST, GETSETURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e( "me", response  );
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                if(response.contains( "Expired" ) || response.contains( "Token" ) || response.contains( "Invalid" )){
                    Toast.makeText( getActivity(), "Token Expired Refreshing...", Toast.LENGTH_LONG ).show();
                    startActivity( new Intent(getActivity(), SplashActivity.class) );
                    getActivity().finish();
                }else if(response.contains( "error" ) || response.contains( "Error" )) {
                    Toast.makeText( getActivity(), getResources().getString( R.string.unknownerror ), Toast.LENGTH_LONG ).show();
                }else if(response.contains( "dt_id" )){
                    fullData = gson.fromJson( response,DebitModel[].class );
                    debitRecView.setLayoutManager(new LinearLayoutManager( getContext() ) );
                    debitAdapter = new DebitAdapter( fullData, getActivity());
                    debitRecView.setAdapter( debitAdapter );

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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("action","getDebitData" );

                map.put("year",yearFromSpinner );

                map.put("month",monthFromSpinner.isEmpty() ? null : monthFromSpinner );
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