package com.example.motobeginner;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ArduinoValues {

    private float x;
    private float y;
    private float z;
    private float rHandPressure;
    private float rHandAccel;
    private float rHandFinger;
    private float lHandFinger;


    public ArduinoValues(){

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getrHandPressure() {
        return rHandPressure;
    }

    public void setrHandPressure(float rHandPressure) {
        this.rHandPressure = rHandPressure;
    }

    public float getrHandAccel() {
        return rHandAccel;
    }

    public void setrHandAccel(float rHandAccel) {
        this.rHandAccel = rHandAccel;
    }

    public float getrHandFinger() {
        return rHandFinger;
    }

    public void setrHandFinger(float rHandFinger) {
        this.rHandFinger = rHandFinger;
    }

    public float getlHandFinger() {
        return lHandFinger;
    }

    public void setlHandFinger(float lHandFinger) {
        this.lHandFinger = lHandFinger;
    }
}
