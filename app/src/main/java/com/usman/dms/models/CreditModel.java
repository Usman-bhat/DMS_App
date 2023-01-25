package com.usman.dms.models;

public class CreditModel
{
    int cr_id,cr_amount;
    String cr_by,cr_for_id,cr_reciept_no,cr_reciept_by,cr_date;

    public CreditModel() {
    }

    public CreditModel(int cr_id, int cr_amount, String cr_by, String cr_for_id, String cr_reciept_no, String cr_reciept_by, String cr_date) {
        this.cr_id = cr_id;
        this.cr_amount = cr_amount;
        this.cr_by = cr_by;
        this.cr_for_id = cr_for_id;
        this.cr_reciept_no = cr_reciept_no;
        this.cr_reciept_by = cr_reciept_by;
        this.cr_date = cr_date;
    }

    public int getCr_id() {
        return cr_id;
    }

    public void setCr_id(int cr_id) {
        this.cr_id = cr_id;
    }

    public int getCr_amount() {
        return cr_amount;
    }

    public void setCr_amount(int cr_amount) {
        this.cr_amount = cr_amount;
    }

    public String getCr_by() {
        return cr_by;
    }

    public void setCr_by(String cr_by) {
        this.cr_by = cr_by;
    }

    public String getCr_for_id() {
        return cr_for_id;
    }

    public void setCr_for_id(String cr_for_id) {
        this.cr_for_id = cr_for_id;
    }

    public String getCr_reciept_no() {
        return cr_reciept_no;
    }

    public void setCr_reciept_no(String cr_reciept_no) {
        this.cr_reciept_no = cr_reciept_no;
    }

    public String getCr_reciept_by() {
        return cr_reciept_by;
    }

    public void setCr_reciept_by(String cr_reciept_by) {
        this.cr_reciept_by = cr_reciept_by;
    }

    public String getCr_date() {
        return cr_date;
    }

    public void setCr_date(String cr_date) {
        this.cr_date = cr_date;
    }
}

/*
"cr_id": "1",
    "cr_amount": "100",
    "cr_date": "2023-01-06 01:00:00",
    "cr_by": "me",
    "cr_mode": "online",
    "cr_for_id": "food",
    "cr_reciept_no": "777",
    "cr_reciept_by": "me"

 */
