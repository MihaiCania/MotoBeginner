package com.example.motobeginner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class FrontBrakeActivity extends AppCompatActivity {

    //GUI
    private GraphView bendGraph;
    private GraphView pressureGraph;
    private ListView listView;

    //Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase database;
    DatabaseReference myRef;

    //For Graph plotting
    private Handler mHandler = new Handler();
    private GraphSeries bendSeries;
    private GraphSeries pressureSeries;
    private String date ="";
    private final String[] items = {"06-Jun-2019", "07-Jun-2019", "08-Jun-2019", "09-Jun-2019", "10-Jun-2019"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_brake);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        bendGraph = (GraphView) findViewById(R.id.bendGraph);
        pressureGraph = (GraphView) findViewById(R.id.pressureGraph);
        listView = (ListView) findViewById(R.id.listViewBrake);

        // this listener will be called when there is change in firebase user session
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(FrontBrakeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FrontBrakeActivity.this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        bendGraph.getViewport().setMinX(0);
        bendGraph.getViewport().setMaxX(6000);
        bendGraph.getViewport().setXAxisBoundsManual(true);
        //TBD
        bendGraph.getViewport().setMinY(60);
        bendGraph.getViewport().setMaxY(180);
        bendGraph.getViewport().setYAxisBoundsManual(true);

        pressureGraph.getViewport().setMinX(0);
        pressureGraph.getViewport().setMaxX(6000);
        pressureGraph.getViewport().setXAxisBoundsManual(true);
        pressureGraph.getViewport().setMinY(0);
        pressureGraph.getViewport().setMaxY(1024);
        pressureGraph.getViewport().setYAxisBoundsManual(true);

        bendGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        pressureGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);

        //TBD
        LineGraphSeries<DataPoint> seriesMinY = new LineGraphSeries<>();
        int y = 90;
        initialSeries(1000, y, seriesMinY);

        seriesMinY.setColor(Color.RED);
        bendGraph.setTitle("Press");
        pressureGraph.setTitle("Force");
        bendGraph.addSeries(seriesMinY);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                date = items[position];
                Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
                bendSeries = new GraphSeries(new LineGraphSeries<DataPoint>(), 0, bendGraph);
                pressureSeries = new GraphSeries(new LineGraphSeries<DataPoint>(), 0, pressureGraph);
                plotGraph("rightHandFinger" , bendSeries);
                plotGraph("rightHandPressure", pressureSeries);
            }
        });
    }

    private void initialSeries(final float max, final float y, final LineGraphSeries<DataPoint> series) {
        new Thread() {
            @Override
            public void run() {
                for(float i = 0; i < max; i++){
                    createSeries(i, y, series);
                }
            }
        }.start();
    }

    private void createSeries(final float x, final float y, final LineGraphSeries<DataPoint> series) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                series.appendData(new DataPoint(x,y), true, 18000);
            }
        }, 10);
    }

    private void addPoints(final float x, final float y, final GraphSeries graph) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                graph.add(new DataPoint(x,y));
            }
        }, 10);
    }

    private void plotGraph(final String str, final GraphSeries graph) {
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();
        // .child(date) the one selected from the ListView
        myRef = FirebaseDatabase.getInstance().getReference().child(userID).child(date);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    addPoints(graph.getCnt(), ds.child(str).getValue(Float.class), graph);
                    graph.incrementCnt();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        graph.getGraph().addSeries(graph.getSeries());
    }
}
