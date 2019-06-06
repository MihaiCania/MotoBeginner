package com.example.motobeginner;

import android.app.Application;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class SaveState extends Application {

    private LineGraphSeries<DataPoint> series;
    private int current;

    public SaveState() {

    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public LineGraphSeries<DataPoint> UnloadData() {
        return series;
    }

    public void LoadData(LineGraphSeries<DataPoint> series) {
        this.series = series;
    }


}
