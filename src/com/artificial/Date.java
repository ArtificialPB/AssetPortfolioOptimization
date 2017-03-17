package com.artificial;

public class Date {
    private final int year, month, day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String formatted() {
        return year + "/" + (month < 10 ? "0" : "") + month + "/" + (day < 10 ? "0" : "") + day;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Date && ((Date) obj).year == year && ((Date) obj).month == month && ((Date) obj).day == day;
    }

    @Override
    public String toString() {
        return year + "_" + month + "_" + day;
    }
}
