package com.example.motobeginner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphSeries {

    private LineGraphSeries<DataPoint> series;
    private int cnt;
    private GraphView graph;

    public GraphSeries(LineGraphSeries<DataPoint> series, int cnt, GraphView graph) {
        this.series = series;
        this.cnt = cnt;
        this.graph = graph;
    }

    public LineGraphSeries<DataPoint> getSeries() {
        return series;
    }

    public void setSeries(LineGraphSeries<DataPoint> series) {
        this.series = series;
    }

    public void add(DataPoint dp) {
        series.appendData(dp, true, 18000);
    }

    public void incrementCnt() {
        cnt++;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public GraphView getGraph() {
        return graph;
    }

    public void setGraph(GraphView graph) {
        this.graph = graph;
    }
}
