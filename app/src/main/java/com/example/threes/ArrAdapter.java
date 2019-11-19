package com.example.threes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArrAdapter extends ArrayAdapter<Integer> {
    private Context ctx;
    private ArrayList<Integer> arrList;
    public ArrAdapter(@NonNull Context context, int resource, @NonNull List<Integer> objects) {
        super(context, resource, objects);
        this.ctx=context;
        this.arrList=new ArrayList<>(objects);
    }

    @Override
    public void notifyDataSetChanged() {
        arrList=Game.getGame().getArr();
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.text_view_item,null);

        }
        if(arrList.size()>0){
            TextViewClass tvc=(TextViewClass)convertView.findViewById(R.id.txtItem);


            tvc.setTileText(arrList.get(position));
        }
        return convertView;
    }
}
