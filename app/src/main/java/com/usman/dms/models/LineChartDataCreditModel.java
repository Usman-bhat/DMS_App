package com.usman.dms.models;

public class LineChartDataCreditModel
{
    int cr_amount;
    String cr_for_id;
    int cr_month;

    public LineChartDataCreditModel() {
    }

    public LineChartDataCreditModel(int cr_amount, String cr_for, int cr_month) {
        this.cr_amount = cr_amount;
        this.cr_for_id = cr_for;
        this.cr_month = cr_month;
    }

    public int getCr_amount() {
        return cr_amount;
    }

    public void setCr_amount(int cr_amount) {
        this.cr_amount = cr_amount;
    }

    public String getCr_for() {
        return cr_for_id;
    }

    public void setCr_for(String cr_for) {
        this.cr_for_id = cr_for;
    }

    public int getCr_month() {
        return cr_month;
    }

    public void setCr_month(int cr_month) {
        this.cr_month = cr_month;
    }
}
