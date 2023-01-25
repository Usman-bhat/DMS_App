package com.usman.dms.models;

public class LineChartDataDebitModel {
    int dt_amount;
    String dt_for;
    int dt_month;

    public LineChartDataDebitModel() {
    }

    public LineChartDataDebitModel(int dt_amount, String dt_for, int dt_month) {
        this.dt_amount = dt_amount;
        this.dt_for = dt_for;
        this.dt_month = dt_month;
    }

    public int getDt_amount() {
        return dt_amount;
    }

    public void setDt_amount(int dt_amount) {
        this.dt_amount = dt_amount;
    }

    public String getDt_for() {
        return dt_for;
    }

    public void setDt_for(String dt_for) {
        this.dt_for = dt_for;
    }

    public int getDt_month() {
        return dt_month;
    }

    public void setDt_month(int dt_month) {
        this.dt_month = dt_month;
    }
}