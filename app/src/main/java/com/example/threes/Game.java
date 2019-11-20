package com.example.threes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    private static Game game;
    private ArrayList<Integer> arr=new ArrayList<>();
    public Tile[][] tiles;
    private int[] colors;
    public int score;
    private Random r=new Random();
    public int nextVal=1;
    public boolean gameOver=false;
    DatabaseHelper databaseHelper;



    private AlertDialog.Builder builder1;

    static {
        game=new Game();
    }
    public static Game getGame(){
        return game;
    }

    public void init(final Context context) {
        databaseHelper=new DatabaseHelper(context);



        builder1 = new AlertDialog.Builder(context);
        final EditText editText=new EditText(context);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(layoutParams);
        builder1.setView(editText);
        builder1.setMessage("Game over, enter friend's email");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        init(context);
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton(
                "Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent emailIntent=new Intent(Intent.ACTION_VIEW);
                        Uri data=Uri.parse("mailto:?subject="+"Check my score"+"&body="+"Hi, played Threes with this score: "+score+"&to="+editText.getText().toString());
                        emailIntent.setData(data);
                        context.startActivity(Intent.createChooser(emailIntent,"Send email..."));
                }
                }
        );



        tiles=new Tile[4][4];
        score=0;
        gameOver=false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tiles[i][j]=new Tile(0,false);
                arr.add(0);
            }
        }
        TypedArray typedArray=context.getResources().obtainTypedArray(R.array.myColors);
        colors=new int[typedArray.length()];
        for(int i=0;i<colors.length;i++){
            colors[i]=typedArray.getColor(i,0);
        }
        typedArray.recycle();
        randomVal();
        refresh();
    }


    public ArrayList<Integer> getArr(){
        return arr;
    }

    public int getColor(int val){
        if(val==0){
            return Color.WHITE;
        }
        else if(val==1){
            return colors[0];
        }
        else if(val==2){
            return colors[1];
        }
        else{
            return colors[2];
        }
    }

    public void randomVal(){
       boolean placed=false;
       int newVal=r.nextInt(3)+1;

        while (!placed){
            int i=r.nextInt(4),j=r.nextInt(4);
            if(tiles[i][j].value==0){
                tiles[i][j].value=nextVal;
                placed=true;
            }
        }
        nextVal=newVal;
        Log.d("next",""+nextVal);

    }

    public void refresh(){
        Log.d("pohyb","jo");
        arr.clear();
        int counter=0;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                //arr.add(gameTiles[i][j]);
                tiles[i][j].modified=false;
                arr.add(tiles[i][j].value);
                if(tiles[i][j].value!=0){
                    counter++;
                }
            }
        }
        if(counter==16){
            Log.d("counter","16");
            checkGameOver();
        }
        if(gameOver){
            AlertDialog alert11 = builder1.create();
            Cursor data=databaseHelper.getData();
            int s=0;
            while(data.moveToNext()){
                s=data.getInt(0);
            }

            if(score>s){
                databaseHelper.update(score);
            }
            alert11.show();
        }
    }



    public void checkGameOver(){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(i<3 && j<3){
                    if((tiles[i][j].value==tiles[i][j+1].value && tiles[i][j].value>2 && tiles[i][j+1].value>2) || (tiles[i][j].value==tiles[i+1][j].value &&
                            tiles[i][j].value>2 && tiles[i+1][j].value>2)){
                        return;
                    }
                    if((tiles[i][j].value==1 && tiles[i][j+1].value==2) || (tiles[i][j].value==2 && tiles[i][j+1].value==1) ||
                            (tiles[i][j].value==1 && tiles[i+1][j].value==2) || (tiles[i][j].value==2 && tiles[i+1][j].value==1)){
                        return;
                    }
                }
                if(i<3 && j==3){
                    if(tiles[i][j].value==tiles[i+1][j].value && tiles[i][j].value>2 && tiles[i+1][j].value>2){
                        return;
                    }
                    if((tiles[i][j].value==1 && tiles[i+1][j].value==2) || (tiles[i][j].value==2 && tiles[i+1][j].value==1)){
                        return;
                    }
                }
                if(i==3 && j<3){
                    if(tiles[i][j].value==tiles[i][j+1].value && tiles[i][j].value>2 && tiles[i][j+1].value>2){
                        return;
                    }
                    if((tiles[i][j].value==1 && tiles[i][j+1].value==2) || (tiles[i][j].value==2 && tiles[i][j+1].value==1)){
                        return;
                    }
                }
            }
        }
        gameOver=true;
    }

    public void moveRight(){
        boolean moved=false;
        for(int i=0;i<4;i++){
            for(int j=2;j>-1;j--){
                if(tiles[i][j].value!=0){
                    int tmp=j;
                    while(tmp+1<4){
                        if(tiles[i][tmp+1].value==0){
                            tiles[i][tmp+1].value=tiles[i][tmp].value;
                            tiles[i][tmp].value=0;
                            tmp++;
                            moved=true;
                        }
                        else if(tiles[i][tmp+1].value==tiles[i][tmp].value && !tiles[i][tmp].modified && !tiles[i][tmp+1].modified && tiles[i][tmp+1].value>2){
                            tiles[i][tmp+1].value*=2;
                            tiles[i][tmp+1].modified=true;
                            tiles[i][tmp].value=0;
                            score+=tiles[i][tmp+1].value;
                            moved=true;
                        }
                        else if(((tiles[i][tmp+1].value==1 && tiles[i][tmp].value==2) || (tiles[i][tmp+1].value==2 && tiles[i][tmp].value==1)) && !tiles[i][tmp].modified && !tiles[i][tmp+1].modified){
                            tiles[i][tmp+1].value=3;
                            tiles[i][tmp+1].modified=true;
                            tiles[i][tmp].value=0;
                            score+=3;
                            moved=true;
                        }
                        else{
                            break;
                        }
                    }
                }
            }
        }
        if(moved){
            randomVal();
        }
        refresh();
    }

    public void moveLeft(){
        boolean moved=false;
        for(int i=0;i<4; i++){
            for(int j=1;j<4;j++){
                if(tiles[i][j].value!=0){
                    int tmp=j;
                    while(tmp-1>-1){
                        if(tiles[i][tmp-1].value==0){
                            tiles[i][tmp-1].value=tiles[i][tmp].value;
                            tiles[i][tmp].value=0;
                            tmp--;
                            moved=true;
                        }
                        else if(tiles[i][tmp].value==tiles[i][tmp-1].value && !tiles[i][tmp].modified && !tiles[i][tmp - 1].modified && tiles[i][tmp].value>2){
                            tiles[i][tmp-1].value*=2;
                            tiles[i][tmp-1].modified=true;
                            tiles[i][tmp].value=0;
                            score+=tiles[i][tmp-1].value;
                            moved=true;
                        }
                        else if(((tiles[i][tmp].value==1 && tiles[i][tmp-1].value==2) || (tiles[i][tmp].value==2 && tiles[i][tmp-1].value==1)) && !tiles[i][tmp].modified && !tiles[i][tmp-1].modified){
                            tiles[i][tmp-1].value=3;
                            tiles[i][tmp-1].modified=true;
                            tiles[i][tmp].value=0;
                            score+=3;
                            moved=true;
                        }
                        else break;
                    }

                }
            }
        }
        if(moved){
            randomVal();
        }
        refresh();
    }

    public void moveDown(){
        boolean moved=false;
        for(int j=0; j<4;j++) {
            for(int i=2; i>-1; i--) {
                if(tiles[i][j].value!=0) {
                    int tmp=i;
                    while(tmp+1<4) {
                        if(tiles[tmp+1][j].value==0) {
                            tiles[tmp+1][j].value=tiles[tmp][j].value;
                            tiles[tmp][j].value=0;
                            tmp++;
                            moved=true;
                        }
                        else if(tiles[tmp+1][j].value==tiles[tmp][j].value && !tiles[tmp + 1][j].modified && !tiles[tmp][j].modified && tiles[tmp+1][j].value>2) {
                            tiles[tmp+1][j].value*=2;
                            tiles[tmp+1][j].modified=true;
                            tiles[tmp][j].value=0;
                            score+=tiles[tmp+1][j].value;
                            moved=true;
                        }
                        else if(((tiles[tmp+1][j].value==1 && tiles[tmp][j].value==2) || (tiles[tmp+1][j].value==2 && tiles[tmp][j].value==1)) && !tiles[tmp+1][j].modified && !tiles[tmp][j].modified){
                            tiles[tmp+1][j].value=3;
                            tiles[tmp+1][j].modified=true;
                            tiles[tmp][j].value=0;
                            score+=3;
                            moved=true;
                        }
                        else break;
                    }

                }
            }
        }
        if(moved) {
            randomVal();
        }
        refresh();
    }

    public void moveUp(){
        boolean moved=false;
        for(int j=0;j<4;j++) {
            for(int i=1;i<4;i++) {
                if(tiles[i][j].value!=0) {
                    int tmp=i;
                    while(tmp>0) {
                        if(tiles[tmp-1][j].value==0) {
                            tiles[tmp-1][j].value=tiles[tmp][j].value;
                            tiles[tmp][j].value=0;
                            tmp--;
                            moved=true;
                        }
                        else if(tiles[tmp-1][j].value==tiles[tmp][j].value && !tiles[tmp - 1][j].modified && !tiles[tmp][j].modified && tiles[tmp-1][j].value>2) {
                            tiles[tmp-1][j].value*=2;
                            tiles[tmp-1][j].modified=true;
                            tiles[tmp][j].value=0;
                            score+=tiles[tmp-1][j].value;
                            moved=true;
                        }
                        else if(((tiles[tmp-1][j].value==1 && tiles[tmp][j].value==2) || (tiles[tmp-1][j].value==2 && tiles[tmp][j].value==1)) && !tiles[tmp-1][j].modified && !tiles[tmp][j].modified){
                            tiles[tmp-1][j].value=3;
                            tiles[tmp-1][j].modified=true;
                            tiles[tmp][j].value=0;
                            score+=3;
                            moved=true;
                        }
                        else break;
                    }

                }
            }
        }
        if(moved)
        {
            randomVal();
        }
        refresh();
    }




}
