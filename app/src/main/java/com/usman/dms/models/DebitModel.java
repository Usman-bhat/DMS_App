package com.usman.dms.models;

public class DebitModel
{
    int dt_amount;
    String dt_for,dt_by,dt_date;

    public DebitModel() {
    }

    public DebitModel(int dt_amount, String dt_for, String dt_by, String dt_date) {
        this.dt_amount = dt_amount;
        this.dt_for = dt_for;
        this.dt_by = dt_by;
        this.dt_date = dt_date;
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

    public String getDt_by() {
        return dt_by;
    }

    public void setDt_by(String dt_by) {
        this.dt_by = dt_by;
    }

    public String getDt_date() {
        return dt_date;
    }

    public void setDt_date(String dt_date) {
        this.dt_date = dt_date;
    }
}

/*
"dt_id": "1",
    "dt_amount": "100000",
    "dt_for": "food",
    "dt_by": "self",
    "dt_date": "2023-01-09 00:11:19"
 */
