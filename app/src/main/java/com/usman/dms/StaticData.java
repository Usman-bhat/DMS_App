package com.usman.dms;

import java.security.PublicKey;
import java.time.Year;
import java.time.YearMonth;
import java.util.Calendar;

/**
 * <h1>Static Data holder Class</h1>
 */
public class StaticData
{
    /**
     * Urls used to connect server
     */
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
}
