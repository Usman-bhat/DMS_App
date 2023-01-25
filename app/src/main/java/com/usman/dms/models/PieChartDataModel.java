package com.usman.dms.models;

public class PieChartDataModel
{
    int amount;
    String year;

    public PieChartDataModel() {
    }

    public PieChartDataModel(int amount, String year) {
        this.amount = amount;
        this.year = year;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
