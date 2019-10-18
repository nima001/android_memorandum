package com.example.com.example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.entity.Memorandum;
import com.example.memorandum.R;

import java.util.List;

public class MemoAdapter extends ArrayAdapter<Memorandum> {
    private int resourceId;
    public MemoAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Memorandum> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Memorandum memorandum = getItem(position);//获取当前note实例，返回当前item显示的数据，方便在Activity中的onItemClick方法中调用。
        // LayoutInflater.from(getContext())是用来获取LayoutInflater 实例，然后.inflate()方法砸入布局
        // 如果inflate(layoutId, null )则layoutId的最外层的控件的宽高是没有效果的
        //如果inflate(layoutId, root, false ) 则认为和上面效果是一样的
        //如果inflate(layoutId, root, true ) 则认为这样的话layoutId的最外层控件的宽高才能正常显示
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView mtitle = view.findViewById(R.id.metitle);

        if(memorandum.getStatus()==1){
            mtitle.setTextColor(Color.parseColor("#DEDEDE"));
            mtitle.setText(memorandum.getTitle());
        }else{
            mtitle.setTextColor(Color.parseColor("#000000"));
            mtitle.setText(memorandum.getTitle());
        }
        return view;//返回值就是返回给ListView每一行的子View
    }

}
