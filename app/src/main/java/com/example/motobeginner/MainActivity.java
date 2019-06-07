package com.example.motobeginner;

import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Button mViewBtn;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase database;
    DatabaseReference myRef;
    //private FirebaseUser user;
    //private String userID;

    private int cnt = 0;

    private Handler mHandler = new Handler();
    private static LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    private GraphView graphView;

    private Long entries = new Long(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        mViewBtn = (Button) findViewById(R.id.btn_view);
        graphView = (GraphView) findViewById(R.id.graphView);


        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(10000);
        graphView.getViewport().setXAxisBoundsManual(true);

        graphView.getViewport().setScalable(true);

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(c);
        Toast.makeText(getApplicationContext(), date , Toast.LENGTH_SHORT).show();

        // this listener will be called when there is change in firebase user session
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }

            }
        };

        //only one extraction in order to find out how many children that user have => how many count entries
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser user = auth.getCurrentUser();
                String userID = user.getUid();
                entries = dataSnapshot.child(userID).getChildrenCount();
                //Toast.makeText(getApplicationContext(), ""+ entries, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                series = new LineGraphSeries<>();
                cnt = 0;
                plotGraph(entries);
            }
        });

    }

    private void addPoints(final float x, final float y) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                series.appendData(new DataPoint(x,y), false, 18000);
            }
        }, 10);
    }

    //take all the entries from the database until a given one and make a graph using them
    private Long i;
    private void plotGraph(Long number) {
            FirebaseUser user = auth.getCurrentUser();
            String userID = user.getUid();
            myRef = FirebaseDatabase.getInstance().getReference().child(userID);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        addPoints(cnt,ds.child("Z").getValue(Float.class));
                        cnt++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        graphView.addSeries(series);
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
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(intent);
                return false;

            case R.id.graphView:

                Intent intent2 = new Intent(MainActivity.this, ClutchAccelActivity.class);
                startActivity(intent2);

                return false;

            case R.id.resetPass:
                Intent intent3 = new Intent(MainActivity.this, ResetPasswordActivity.class);
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
