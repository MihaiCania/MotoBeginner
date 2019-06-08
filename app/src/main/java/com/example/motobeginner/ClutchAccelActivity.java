package com.example.motobeginner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class ClutchAccelActivity extends AppCompatActivity {

    //GUI
    private GraphView accelGraph;
    private GraphView clutchGraph;
    private ListView listView;

    //Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase database;
    DatabaseReference myRef;

    //For Graph plotting
    private Handler mHandler = new Handler();
    private GraphSeries accelSeries;
    private GraphSeries clutchSeries;
    private String date ="";
    private final String[] items = {"06-Jun-2019", "07-Jun-2019", "08-Jun-2019", "09-Jun-2019", "10-Jun-2019"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clutch_accel);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        accelGraph = (GraphView) findViewById(R.id.accelGraph);
        clutchGraph = (GraphView) findViewById(R.id.clutchGraph);
        listView = (ListView) findViewById(R.id.listView);

        // this listener will be called when there is change in firebase user session
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ClutchAccelActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ClutchAccelActivity.this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        accelGraph.getViewport().setMinX(0);
        accelGraph.getViewport().setMaxX(6000);
        accelGraph.getViewport().setXAxisBoundsManual(true);
        accelGraph.getViewport().setMinY(100);
        accelGraph.getViewport().setMaxY(300);
        accelGraph.getViewport().setYAxisBoundsManual(true);
        accelGraph.getViewport().setScalable(true);

        clutchGraph.getViewport().setMinX(0);
        clutchGraph.getViewport().setMaxX(6000);
        clutchGraph.getViewport().setXAxisBoundsManual(true);
        clutchGraph.getViewport().setMinY(60);
        clutchGraph.getViewport().setMaxY(150);
        clutchGraph.getViewport().setYAxisBoundsManual(true);
        clutchGraph.getViewport().setScalable(true);

        accelGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        clutchGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);

        LineGraphSeries<DataPoint> seriesMinY = new LineGraphSeries<>();
        int y = 90;
        initialSeries(1000, y, seriesMinY);

        seriesMinY.setColor(Color.RED);
        accelGraph.setTitle("Acceleration");
        clutchGraph.setTitle("Clutch");
        accelGraph.addSeries(seriesMinY);
        clutchGraph.addSeries(seriesMinY);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                date = items[position];
                Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
                accelSeries = new GraphSeries(new LineGraphSeries<DataPoint>(), 0, accelGraph);
                clutchSeries = new GraphSeries(new LineGraphSeries<DataPoint>(), 0, clutchGraph);
                plotGraph("Acceleration" , accelSeries);
                plotGraph("Clutch", clutchSeries);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth:
                Intent intent = new Intent(ClutchAccelActivity.this, BluetoothActivity.class);
                startActivity(intent);
                return false;

            case R.id.graphView:
                Intent intent2 = new Intent(ClutchAccelActivity.this, MainActivity.class);
                startActivity(intent2);
                return false;

            case R.id.resetPass:
                Intent intent3 = new Intent(ClutchAccelActivity.this, ResetPasswordActivity.class);
                startActivity(intent3);
                return false;

            case R.id.signOut:
                signOut();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void signOut() {
        auth.signOut();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth.signOut();
    }
}
