package com.usman.dms;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.usman.dms.models.DebitModel;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PublicKey;
import java.time.Year;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;

/**
 * <h1>Static Data holder Class</h1>
 */
public class StaticData
{
    /**
     * Urls used to connect server
     */
//     public static String URL="http://192.168.1.4";
//    public static String URL="http://192.168.158.133";
//     public static String URL="http://192.168.233.133";
     public static String URL="https://misbahululoom.000webhostapp.com";
    public static String LOGINURL=URL+"/Android_api/login.php";
    public static String GETSETURL=URL+"/Android_api/getSetData.php";


    /**
     * Shared preference File Names
     */
    public static String LOGINSP = "LoginSp";
    public static String ACCESS_TOKEN_SP = "accessToken";
    public static String REFRESH_TOKEN_SP = "refreshToken";

    /**
     * CHART
     */
    //ID's
    public static final int CREDITCHARTID = 0;
    public static final int DEBITCHARTID = 1;
    // Label's
    public static final String CREDIT_PIE_LABEL="Credit Data";
    public static final String DEBIT_PIE_LABEL="DebitData";
    //Center text
    public static final String CREDIT_PIE_CENTER_TEXT="آمدنی";
    public static final String DEBIT_PIE_CENTER_TEXT="خرچات";


    /**
     * for ForType
     */

    public static final String FOOD = "food";
    public static final String PAY = "pay";
    public static final String BILLS = "bills";
    public static final String CONSTRUCTION = "construction";
    public static final String OTHERS = "others";
    /**
     * other Static Data
     */
    public static String YEAR = String.valueOf( getYear() );
    public static final String TAG = "me";
    public static final String NAME = "MISBAH_UL_ULOOM";
    public static final int  ENTRIESPERPAGE = 18;
    public static final int NOTIFICATIONID = 1;
    public static final String CHANNELID = "Misbah Ul Uloom";



    public static  int getYear(){
        int year,day,month;
        day=Calendar.getInstance().get( Calendar.DAY_OF_MONTH );
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            year = Year.now().getValue();
            month = YearMonth.now().getMonthValue();
        }else{
            year = Calendar.getInstance().get( Calendar.YEAR );
            month = Calendar.getInstance().get( Calendar.MONTH );
        }
        if(month == 1 && day<2){
            return year-1;
        }
        return  year;
    }

//    public boolean validate_access_Token{
//
//}

    /**
     * generate pdf from model Class data
     * @param fullData data of type model
     */
    private void generatePDFfrmData(DebitModel[] fullData, Activity activity) throws Exception {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        final int width = 792;
        final int height = 1120;
        int entry_count = 0;
        int page_no = 1;
        PdfDocument.PageInfo mypageInfo;
        PdfDocument.Page page;

        int total_pages = fullData.length/ENTRIESPERPAGE;
        if(total_pages<1){
            total_pages=1;
        }

        PdfDocument pdfDocument = new PdfDocument();//
        Paint body = new Paint();
        Paint title = new Paint();
        Paint footer = new Paint();


        String text = "";
        int sum=0;
        int y = 150;
        boolean newPageFlag = false;
        Canvas c;
        mypageInfo = new PdfDocument.PageInfo.Builder(width, height,page_no).create();
        page = pdfDocument.startPage(mypageInfo);
        c = page.getCanvas();
        Typeface noori = ResourcesCompat.getFont( activity,R.font.nooriregular );
        //title
        title.setTypeface( noori );
        title.setTextSize( 30 );
        title.setTextAlign( Paint.Align.CENTER);
        title.setColor( Color.GREEN );
        c.drawText( activity.getString( R.string.pdf_header ), width/2,50,title );
        //body
        body.setTextSize( 30 );
        body.setTypeface( noori );
        body.setTextAlign( Paint.Align.LEFT );
        body.setColor( Color.DKGRAY );
        body.setStrokeWidth( 2f );

        //topline
        c.drawLine( 20,80,width-30,80,body );
//        c.drawText( getResources().getString( R.string.date )+" -------------- "+getResources().getString( R.string.amount )+" ---------- "+getResources().getString( R.string.debited_by )+" -------- "+getResources().getString( R.string.debited_for ),20,90,body );
        //headers
        c.drawText( activity.getString( R.string.date ),30,110,body );
        c.drawText( activity.getString( R.string.amount ),240,110,body );
        c.drawText( activity.getString( R.string.debited_by ),440,110,body );
        c.drawText( activity.getString( R.string.debited_for ),640,110,body );

        //2nd header line
        c.drawLine( 20,120,width-30,120,body );

        //khada lines
        c.drawLine( 200,120,200,height-70,body );
        c.drawLine( 400,120,400,height-70,body );
        c.drawLine( 600,120,600,height-70,body );

        //end table line
        c.drawLine( 20,height-70,width-30,height-70,body );
        for (DebitModel data :fullData) {
            if(newPageFlag ){
                page_no ++;
                mypageInfo = new PdfDocument.PageInfo.Builder(width, height,page_no).create();
                page = pdfDocument.startPage(mypageInfo);
                c = page.getCanvas();
                //title
                title.setTypeface( noori );
                title.setTextSize( 30 );
                title.setTextAlign( Paint.Align.CENTER);
                title.setColor( Color.GREEN );
                c.drawText( activity.getString( R.string.pdf_header ), width/2,50,title );
                //body
                body.setTextSize( 30 );
                body.setTypeface( noori );
                body.setTextAlign( Paint.Align.LEFT );
                body.setColor( Color.DKGRAY );
                body.setStrokeWidth( 2f );
                //topline
                c.drawLine( 20,80,width-30,80,body );
                //headers
                c.drawText( activity.getString( R.string.date ),30,110,body );
                c.drawText( activity.getString( R.string.amount ),240,110,body );
                c.drawText( activity.getString( R.string.debited_by ),440,110,body );
                c.drawText( activity.getString( R.string.debited_for ),640,110,body );
                //2nd header line
                c.drawLine( 20,120,width-30,120,body );
                //khada lines
                c.drawLine( 200,120,200,height-70,body );
                c.drawLine( 400,120,400,height-70,body );
                c.drawLine( 600,120,600,height-70,body );

                //end table line
                c.drawLine( 20,height-70,width-30,height-70,body );
                newPageFlag =false;
            }
            //Enter Rows Each loop Denote one row
            String[] date1 = data.getDt_date().split( " " );
            text += date1[0];
            text += "       ";
            text += data.getDt_amount();
            text += "                  ";
            text += data.getDt_by();
            text += "                    ";
            text += data.getDt_for();
            c.drawText( text,20,y,body );
            sum+=data.getDt_amount();
            y+=50;
            text="";
            entry_count ++;
            if(entry_count == ENTRIESPERPAGE){
                pdfDocument.finishPage( page );
                newPageFlag = true;
            }
            
        }

        //For Showing total sum at the end of PDF
        body.setTextSize( 30 );
        c.drawText( "       TOTAL = "+sum, 20,height-45,body );
        //to here
        //footer
        footer.setTextSize( 20 );
        footer.setTypeface( noori );
        footer.setTextAlign( Paint.Align.LEFT );
        footer.setColor( Color.BLUE );
        c.drawText( activity.getString( R.string.pdf_footer ),20,height-20,footer);

        pdfDocument.finishPage( page );

        /**    END */

        String pdfPath = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS ).toString();
        File file = new File( pdfPath,NAME+"DebitRecord"+new Date().getTime()+".pdf" );

        pdfDocument.writeTo( new FileOutputStream( file ) );
        pdfDocument.close();




    }


}
