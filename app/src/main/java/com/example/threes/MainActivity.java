package com.example.threes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ShakeDetector.Listener {
private GridView gamePlan;
private View.OnTouchListener onTouchListener;
private TextView nextValue;
private TextView score;
private TextView highScore;
private float x,y;
private MediaPlayer moveSound;
DatabaseHelper databaseHelper;
SharedPreferences sharedPreferences;
SharedPreferences.Editor editor;
long time;
Date date;
//private MediaPlayer moveSound=MediaPlayer.create(this,R.raw.move);

private ArrAdapter arrAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nextValue=findViewById(R.id.nextValue);
        score=findViewById(R.id.score);
        highScore=findViewById(R.id.highScore);
        moveSound=MediaPlayer.create(MainActivity.this,R.raw.move);
        databaseHelper=new DatabaseHelper(this);
        sharedPreferences=getSharedPreferences("prefs", Context.MODE_PRIVATE);

        long getTime=sharedPreferences.getLong("time",0);
        date=new Date();
        time=date.getTime();
        long diff=time-getTime;
        long days=TimeUnit.MILLISECONDS.toDays(diff);
        diff-=TimeUnit.DAYS.toMillis(days);
        long hours=TimeUnit.MILLISECONDS.toHours(diff);
        diff-=TimeUnit.HOURS.toMillis(hours);
        long mins=TimeUnit.MILLISECONDS.toMinutes(diff);
        diff-=TimeUnit.MINUTES.toMillis(mins);
        long sec=TimeUnit.MILLISECONDS.toSeconds(diff);

        Toast.makeText(MainActivity.this,""+days+" "+hours+" "+mins+" "+sec,Toast.LENGTH_SHORT).show();

        Cursor data=databaseHelper.getData();
        int s=0;
        while(data.moveToNext()){
            s=data.getInt(0);
        }

        highScore.setText(""+s);



        SensorManager sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector shakeDetector=new ShakeDetector(this);
        shakeDetector.start(sensorManager);
        setGrid();
        initGame();
        setData();
    }
    private void setGrid(){
        gamePlan=(GridView)findViewById(R.id.gridView);
    }

    private void initGame(){
        Game.getGame().init(MainActivity.this);

        arrAdapter=new ArrAdapter(MainActivity.this,0,Game.getGame().getArr());
        onTouchListener=new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x=event.getX();
                        y=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if(Math.abs(event.getX()-x)>Math.abs(event.getX()-y)){
                            if(event.getX()>x && event.getX()-x>300){
                                Toast.makeText(MainActivity.this,"doprava",Toast.LENGTH_SHORT).show();
                                Game.getGame().moveRight();
                                nextValue.setText(""+Game.getGame().nextVal);
                                score.setText(""+Game.getGame().score);
                                moveSound.start();
                                //moveSound.start();
                                arrAdapter.notifyDataSetChanged();
                            }else if(event.getX()<x && x-event.getX()>300) {
                                Toast.makeText(MainActivity.this,"doleva",Toast.LENGTH_SHORT).show();
                                Game.getGame().moveLeft();
                                nextValue.setText(""+Game.getGame().nextVal);
                                score.setText(""+Game.getGame().score);
                                moveSound.start();
                                //moveSound.start();
                                arrAdapter.notifyDataSetChanged();
                            }

                        }else{
                            if(event.getY()>y){
                                Toast.makeText(MainActivity.this,"dolu",Toast.LENGTH_SHORT).show();
                                Game.getGame().moveDown();
                                nextValue.setText(""+Game.getGame().nextVal);
                                score.setText(""+Game.getGame().score);
                                moveSound.start();
                                //moveSound.start();
                                arrAdapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(MainActivity.this,"nahoru",Toast.LENGTH_SHORT).show();
                                Game.getGame().moveUp();
                                nextValue.setText(""+Game.getGame().nextVal);
                                score.setText(""+Game.getGame().score);
                                moveSound.start();
                                //moveSound.start();
                                arrAdapter.notifyDataSetChanged();
                            }
                        }
                        date=new Date();
                        time=date.getTime();
                        editor=sharedPreferences.edit();
                        editor.putLong("time",time);
                        editor.apply();

                        break;
                }
                return true;
            }
        };
    }

    private void setData(){
        gamePlan.setAdapter(arrAdapter);
        gamePlan.setOnTouchListener(onTouchListener);
    }

    @Override
    public void hearShake() {
        Toast.makeText(MainActivity.this,"shake",Toast.LENGTH_SHORT).show();
        setGrid();
        initGame();
        setData();
    }

    public void addData(int item){
        boolean insertData=databaseHelper.addData(item);
        if(insertData){
            Toast.makeText(MainActivity.this,"db jo",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this,"db ne",Toast.LENGTH_SHORT).show();
        }
    }




}
