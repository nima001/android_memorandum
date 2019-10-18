package com.example.com.example.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.entity.Classify;
import com.example.helper.MyDatabaseHelper;
import com.example.memorandum.ModifyActivity;
import com.example.memorandum.R;

import java.util.List;

public class ClassifyAdapter extends ArrayAdapter<Classify> {
    private int resourceId;
    private List<Classify> classlist;

    public ClassifyAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Classify> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        classlist = objects;
    }

    //第一个参数选中对象在集合中的位置，第二个当前显示的条目对象，第三个应该就是宿主对象，
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Classify classify = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView mclass = view.findViewById(R.id.mclass);
        Button mdelclassbtn = view.findViewById(R.id.delclass);
        mclass.setText(classify.getClassname());

        mdelclassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(getContext(), "Memorandum.db", null, 1);
                SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
                db.delete("memorandum", "classify=?", new String[]{classify.getClassname() + ""});
                db.delete("classify", "id=?", new String[]{classify.getId() + ""});

                //删除listview绑定的集合数据中的相应数据
                classlist.remove(position);
                //notifyDataSetChanged方法通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
                ClassifyAdapter.this.notifyDataSetChanged();
            }
        });
        return view;
    }
}
