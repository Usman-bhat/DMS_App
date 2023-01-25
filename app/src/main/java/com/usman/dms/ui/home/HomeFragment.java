package com.usman.dms.ui.home;

import static com.usman.dms.StaticData.ACCESS_TOKEN_SP;
import static com.usman.dms.StaticData.BILLS;
import static com.usman.dms.StaticData.CONSTRUCTION;
import static com.usman.dms.StaticData.CREDITCHARTID;
import static com.usman.dms.StaticData.DEBITCHARTID;
import static com.usman.dms.StaticData.FOOD;
import static com.usman.dms.StaticData.GETSETURL;
import static com.usman.dms.StaticData.LOGINSP;
import static com.usman.dms.StaticData.OTHERS;
import static com.usman.dms.StaticData.PAY;
import static com.usman.dms.StaticData.YEAR;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.usman.dms.LoginActivity;
import com.usman.dms.R;
import com.usman.dms.SplashActivity;
import com.usman.dms.chartData.MyMarkerView;
import com.usman.dms.databinding.FragmentHomeBinding;
import com.usman.dms.models.LineChartDataCreditModel;
import com.usman.dms.models.LineChartDataDebitModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    LineChart creditChart;
    LineChart debitChart;
    TextView creditError,debitError;
    private String ACCESS_TOKEN;;
    boolean CREDIT_DATA_READY= false;
    boolean DEBIT_DATA_READY= false;
    ProgressBar creditChartPbar,debitChartPbar;
    ArrayList<Entry> chartDataFood;
    ArrayList<Entry> chartDataBills;
    ArrayList<Entry> chartDataPay;
    ArrayList<Entry> chartDataConstruction;
    ArrayList<Entry> chartDataOthers;



    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider( this ).get( HomeViewModel.class );

        binding = FragmentHomeBinding.inflate( inflater, container, false );
        View root = binding.getRoot();
        /**
         * Get Tokens From {@link SharedPreferences}
         */
        SharedPreferences sp = getActivity().getSharedPreferences(LOGINSP, Context.MODE_PRIVATE);
        if(!sp.contains( ACCESS_TOKEN_SP )){
            startActivity( new Intent(getActivity(), LoginActivity.class ) );
        }else{
            ACCESS_TOKEN = sp.getString( ACCESS_TOKEN_SP,null );
        }
        /**
         * To avoid null pointer exception
         */
        chartDataFood= new ArrayList<>();
        chartDataBills= new ArrayList<>();
        chartDataConstruction= new ArrayList<>();
        chartDataPay= new ArrayList<>();
        chartDataOthers= new ArrayList<>();

        /**  Bind views */
        creditChart = binding.creditChart;
        creditError = binding.creditErrorTv;
        creditChartPbar = binding.creditChartPbar;

        debitChart = binding.debitChart;
        debitError = binding.debitErrorTv;
        debitChartPbar = binding.debitChartPbar;

        setLoading(CREDITCHARTID);
        setLoading(DEBITCHARTID);
        makeRequestForData(creditChart,CREDITCHARTID,"amount","getLineChatData",YEAR);
        makeRequestForData(debitChart,DEBITCHARTID,"amount","getLineChatData",YEAR);
        return root;
    }

    private void setLoading(int chartId) {
        if(chartId == CREDITCHARTID){
            if(!CREDIT_DATA_READY){
                creditChartPbar.setVisibility( View.VISIBLE );
                creditChart.setVisibility( View.GONE );
            }else{
                creditChartPbar.setVisibility( View.GONE );
                creditChart.setVisibility( View.VISIBLE );
            }
        }else{
            if(!DEBIT_DATA_READY){
                debitChartPbar.setVisibility( View.VISIBLE );
                debitChart.setVisibility( View.GONE );
            }else{
                debitChartPbar.setVisibility( View.GONE );
                debitChart.setVisibility( View.VISIBLE );
            }

        }
    }

    /**
     * @param chartId of Type int where 0 = CreditChart AND 1=DebitChart
     * Shows Error if App fails to get Data from server or any other error Occurred for a specific Chart specified in {chartId}
     */
    private void setError(int chartId) {
        if(chartId == 0){
            creditChart.setVisibility( View.GONE );
            creditError.setVisibility( View.VISIBLE );
            creditError.setText( "Cannot fetch Data" );
        }else{
            debitChart.setVisibility( View.GONE );
            debitError.setVisibility( View.VISIBLE );
            debitError.setText( "Cannot Fetch Data" );
        }

    }

    /**
     * <h3>Volley Request Stuff Here</h3>
     * @param chart LineChart View in which you want to set data
     * @param chartId id of chart i,e. credit or debit
     * @param onSuccess String returned from server on success
     * @param action POST[action] param to server request
     * @param year of which you want to get data
     */

    private void  makeRequestForData(LineChart chart,int chartId,String onSuccess,String action,String year) {
        RequestQueue queue = Volley.newRequestQueue( getContext() );


        StringRequest request = new StringRequest( Request.Method.POST, GETSETURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e( "me",response );
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                if(response.contains( onSuccess )){
                    /** <h1> Credit Chart</h1>*/
                    if(chartId ==CREDITCHARTID){
                        chartDataFood= new ArrayList<>();
                        chartDataBills= new ArrayList<>();
                        chartDataConstruction= new ArrayList<>();
                        chartDataPay= new ArrayList<>();
                        chartDataOthers= new ArrayList<>();
                        LineChartDataCreditModel[] data = gson.fromJson( response,LineChartDataCreditModel[].class );
                        /**
                         * Set data in there corresponding models
                         */
                        for (int i=0;i< data.length;i++){
                            String amountFor  = data[i].getCr_for();
                            switch (amountFor){
                                case FOOD:
                                    chartDataFood.add( new Entry(data[i].getCr_month(),data[i].getCr_amount(),amountFor) );
                                    break;
                                case PAY:
                                    chartDataPay.add( new Entry(data[i].getCr_month(),data[i].getCr_amount(),amountFor) );
                                    break;
                                case BILLS:
                                    chartDataBills.add( new Entry(data[i].getCr_month(),data[i].getCr_amount(),amountFor) );
                                    break;
                                case CONSTRUCTION:
                                    chartDataConstruction.add( new Entry(data[i].getCr_month(),data[i].getCr_amount(),amountFor) );
                                    break;
                                case OTHERS:
                                    chartDataOthers.add( new Entry(data[i].getCr_month(),data[i].getCr_amount(),amountFor) );
                                    break;
                                default:
                                    break;
                            }
                        }

                            CREDIT_DATA_READY=true;
                            setLoading(CREDITCHARTID);
                            LineDataSet[] data11 = {
                                    setCreditChartLineDataSet(chart,chartDataFood,getResources().getString( R.string.food ),Color.YELLOW),
                                    setCreditChartLineDataSet(chart,chartDataBills,getResources().getString( R.string.bills ),Color.BLACK),
                                    setCreditChartLineDataSet(chart,chartDataPay,getResources().getString( R.string.pay ),Color.GREEN),
                                    setCreditChartLineDataSet(chart,chartDataConstruction,getResources().getString( R.string.construction ),Color.GRAY),
                                    setCreditChartLineDataSet(chart,chartDataOthers,getResources().getString( R.string.others ),Color.RED),
                            };
                            setCreditChartDataSet(chart,data11);
                    }else{
                        /**  <h1> Debit Chart</h1> */
                        chartDataFood= new ArrayList<>();
                        chartDataBills= new ArrayList<>();
                        chartDataConstruction= new ArrayList<>();
                        chartDataPay= new ArrayList<>();
                        chartDataOthers= new ArrayList<>();

                        LineChartDataDebitModel[] data = gson.fromJson( response,LineChartDataDebitModel[].class );
                        /**
                         * Set data in there corresponding models
                         */
                        for (int i=0;i< data.length;i++){
                            String amountFor1  = data[i].getDt_for();
                            switch (amountFor1){
                                case FOOD:
                                    chartDataFood.add( new Entry(data[i].getDt_month(),data[i].getDt_amount(),amountFor1) );
                                    break;
                                case PAY:
                                    chartDataPay.add( new Entry(data[i].getDt_month(),data[i].getDt_amount(),amountFor1) );
                                    break;
                                case BILLS:
                                    chartDataBills.add( new Entry(data[i].getDt_month(),data[i].getDt_amount(),amountFor1) );
                                    break;
                                case CONSTRUCTION:
                                    chartDataConstruction.add( new Entry(data[i].getDt_month(),data[i].getDt_amount(),amountFor1) );
                                    break;
                                case OTHERS:
                                    chartDataOthers.add( new Entry(data[i].getDt_month(),data[i].getDt_amount(),amountFor1) );
                                    break;
                                default:
                                    break;
                            }
                        }
                        DEBIT_DATA_READY=true;
                        setLoading(DEBITCHARTID);
                        LineDataSet[] data11 = {
                                setDebitChartLineDataSet(chart,chartDataFood,getResources().getString( R.string.food ),Color.YELLOW),
                                setDebitChartLineDataSet(chart,chartDataBills,getResources().getString( R.string.bills ),Color.BLACK),
                                setDebitChartLineDataSet(chart,chartDataPay,getResources().getString( R.string.pay ),Color.GREEN),
                                setDebitChartLineDataSet(chart,chartDataConstruction,getResources().getString( R.string.construction ),Color.GRAY),
                                setDebitChartLineDataSet(chart,chartDataOthers,getResources().getString( R.string.others ),Color.RED),
                        };
                        setDebitChartDataSet(chart,data11);

                    }


                }else if(response.contains( "Expired" ) || response.contains( "Token" ) || response.contains( "Invalid" )){
                    Toast.makeText( getActivity(), "Token Expired Refreshing...", Toast.LENGTH_LONG ).show();
                    startActivity( new Intent(getActivity(), SplashActivity.class) );
                    getActivity().finish();
                }else if(response.contains( "[]" )){
                    Toast.makeText( getActivity(), "No Data Found for this Month", Toast.LENGTH_LONG ).show();
                }else if(response.contains( "error" )&& response.contains( "message" )){
                    setError( chartId );
                }
                else {
                    Toast.makeText( getActivity(), "Error!Error Occurred", Toast.LENGTH_LONG ).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText( getActivity(), "500: Error Occurred", Toast.LENGTH_LONG ).show();
                Log.e( "me", error.toString());
            }
        } ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                Log.e( "me", "getHeaders: "+ ACCESS_TOKEN );
                params.put("authorization", "Bearer "+ACCESS_TOKEN);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("action",action );

                map.put("year",year );
                if(chartId == CREDITCHARTID) {
                    map.put("table","cr" );
                }else{
                    map.put("table","dt" );
                }
                return map;
            }
        };
        queue.add( request );

    }

    /**
     * To set array or datasets in single graph
     * @param chart view in  which we want to set data
     * @param lineDataSets array of datasets to fix in graph
     */
    private void setCreditChartDataSet(LineChart chart,LineDataSet[] lineDataSets) {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for (LineDataSet data1:lineDataSets) {
            dataSets.add( data1 );
        }

        LineData lineData = new LineData(dataSets);
        chart.setData( lineData );
        chart.setDrawGridBackground( false );
        chart.setBackgroundColor( Color.rgb( 44,137,174 ) );
//        chart.invalidate();

        /**  disable description text*/
        chart.getDescription().setEnabled(false);

        /**  create marker to display box when values are selected*/
        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_chart_marker);

        /**  Set the marker to the chart*/
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.getXAxis().setTextColor( Color.BLACK );
        chart.animateX(1000);
        chart.animateY(1000);
        chart.animate();
    }


    /**
     * To set array or datasets in single graph
     * @param chart view in  which we want to set data
     * @param lineDataSets array of datasets to fix in graph
     */
    private void setDebitChartDataSet(LineChart chart,LineDataSet[] lineDataSets) {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for (LineDataSet data1:lineDataSets) {
            dataSets.add( data1 );
        }

        LineData lineData = new LineData(dataSets);
        chart.setData( lineData );
        chart.setDrawGridBackground( false );
        chart.setBackgroundColor( Color.rgb( 44,137,174 ) );
//        chart.invalidate();

        /**  disable description text*/
        chart.getDescription().setEnabled(false);

        /**  create marker to display box when values are selected*/
        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_chart_marker);

        /**  Set the marker to the chart*/
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.getXAxis().setTextColor( Color.BLACK );
        chart.animateX(1000);
        chart.animateY(1000);
        chart.animate();
    }


    /**
     * <h3>Do Debit Chart Stuff Here</h3>
     * After getting data from server set it to the debitChartView Sets it To {@link  LineChart } Given As Param
     * @param chart  of Type {@link  LineChart } view to which Data has to set
     * @param chartData data from server in the form of {@link ArrayList<Entry>} of type {@link Entry}
     */

    private void setDebitChartData(LineChart chart,ArrayList<Entry> chartData) {
        LineDataSet lineDataSet = new LineDataSet(chartData,"Debit Data Chart "  );
        lineDataSet.setDrawCircleHole( false );
        lineDataSet.setColors( Color.BLACK );
        lineDataSet.setValueTextColor( Color.BLACK );
        lineDataSet.setValueTextSize( 16f );
        lineDataSet.setMode( LineDataSet.Mode.CUBIC_BEZIER );
        lineDataSet.setCubicIntensity( 0.2f );
        lineDataSet.setCircleRadius( 4f );
        lineDataSet.setDrawFilled( true );
        /**  set the filled area*/
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawValues( false );
        lineDataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });

         /** set color of filled area*/
        if (Utils.getSDKInt() >= 18) {
            /**  drawables only supported on api level 18 and above*/
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_chart_color);
            lineDataSet.setFillDrawable(drawable);
        } else {
            lineDataSet.setFillColor(Color.DKGRAY);
        }


        LineData lineData = new LineData(lineDataSet);
        chart.setData( lineData );
        chart.setDrawGridBackground( false );
        chart.setBackgroundColor( Color.rgb( 244,117,177 ) );

        /**  disable description text*/
        chart.getDescription().setEnabled(false);

        /**  create marker to display box when values are selected */
        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_chart_marker);

        /**  Set the marker to the chart*/
        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.getXAxis().setTextColor( Color.BLACK );
        chart.animateY(1500);
        chart.animate();
    }

    /**
     * <h3>Do Credit Chart Stuff Here</h3>
     * After getting data from server set it to the creditChartView Sets it To {@link  LineChart } Given As Param
     * @param chart  of Type {@link  LineChart } view to which Data has to set
     * @param chartData data from server in the form of {@link ArrayList<Entry>} of type {@link Entry}
     * @param label Label to set on chart
     * @param color color of outer line of graph
     */
    private LineDataSet  setCreditChartLineDataSet(LineChart chart,ArrayList<Entry> chartData,String label,int color) {
        LineDataSet lineDataSet = new LineDataSet( chartData,label );
        lineDataSet.setDrawCircleHole( false );
        lineDataSet.setColors( color );
        lineDataSet.setValueTextColor( Color.BLACK );
        lineDataSet.setValueTextSize( 16f );
        lineDataSet.setMode( LineDataSet.Mode.CUBIC_BEZIER );
        lineDataSet.setCubicIntensity( 0.2f );
        lineDataSet.setCircleRadius( 4f );
        lineDataSet.setDrawFilled( true );
        /** set the filled area */
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawValues( false );
//        lineDataSet.color

        lineDataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });

        /**  set color of filled area*/
//        if (Utils.getSDKInt() >= 18) {
//            // drawables only supported on api level 18 and above
//            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_chart_color);
//            lineDataSet.setFillDrawable(drawable);
//        } else {
            lineDataSet.setFillColor(color);
//        }
        lineDataSet.setValueFormatter( new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return  String.format( Locale.getDefault(),"%.0f",value );
            }
        } );

        return lineDataSet;

    }


    /**
     * <h3>Do Debit Chart Stuff Here</h3>
     * After getting data from server set it to the creditChartView Sets it To {@link  LineChart } Given As Param
     * @param chart  of Type {@link  LineChart } view to which Data has to set
     * @param chartData data from server in the form of {@link ArrayList<Entry>} of type {@link Entry}
     * @param label Label to set on chart
     * @param color color of outer line of graph
     */
    private LineDataSet  setDebitChartLineDataSet(LineChart chart,ArrayList<Entry> chartData,String label,int color) {
        LineDataSet lineDataSet = new LineDataSet( chartData,label );
        lineDataSet.setDrawCircleHole( false );
        lineDataSet.setColors( color );
        lineDataSet.setValueTextColor( Color.BLACK );
        lineDataSet.setValueTextSize( 16f );
        lineDataSet.setMode( LineDataSet.Mode.CUBIC_BEZIER );
        lineDataSet.setCubicIntensity( 0.2f );
        lineDataSet.setCircleRadius( 4f );
        lineDataSet.setDrawFilled( true );
        /** set the filled area */
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawValues( false );
//        lineDataSet.color

        lineDataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });

        /**  set color of filled area*/
//        if (Utils.getSDKInt() >= 18) {
//            // drawables only supported on api level 18 and above
//            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_chart_color);
//            lineDataSet.setFillDrawable(drawable);
//        } else {
            lineDataSet.setFillColor(color);
//        }
        lineDataSet.setValueFormatter( new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return  String.format( Locale.getDefault(),"%.0f",value );
            }
        } );

        return lineDataSet;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        CREDIT_DATA_READY=false;
        DEBIT_DATA_READY=false;
//        if(!chartDataFood.isEmpty()){
//
//        }
        chartDataFood.clear();
        chartDataBills.clear();
        chartDataPay.clear();
        chartDataConstruction.clear();
        chartDataOthers.clear();
    }
}