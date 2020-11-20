package com.example.sqllitemanytomany;

public class Statistic {

    private Integer day;
    private Integer month;
    private Float percent;

    public Statistic()
    {

    }

    public Statistic(Integer day, Integer month, Float percent) {
        this.day = day;
        this.month = month;
        this.percent = percent;
    }

    public Integer getMonth() { return month; }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Float getPercent() {
        return percent;
    }

    public void setPercent(Float percent) {
        this.percent = percent;
    }

    public Integer getDay() { return day; }

    public void setDay(Integer day) { this.day = day; }

}
