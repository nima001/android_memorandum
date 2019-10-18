package com.example.memorandum;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.EditText;
import android.widget.ListView;

import com.example.adapter.ClassifyAdapter;
import com.example.entity.Classify;
import com.example.helper.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ModifyActivity extends AppCompatActivity {

   public  List<Classify> classlist;
    MyDatabaseHelper mhelper;
    SQLiteDatabase sqLiteDatabase;
    Classify classify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        //启用系统自带的导航栏返回键,AndroidManifest.xml里面配置父类activity，即可使用该按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        classlist=new ArrayList<>();
        classify=new Classify();

        mhelper=new MyDatabaseHelper(this, "Memorandum.db", null, 1);
        sqLiteDatabase=mhelper.getWritableDatabase();
        //加载数据
        initclassname();
        classadaper();

    }

    //查询全部分组信息
   public void  initclassname(){
       Cursor cursor = sqLiteDatabase.query("classify", null, null, null, null, null, null);
       //查询全部分组信息，存入classlist
       if (cursor.moveToFirst()) {//cursor.moveToFirst()指向查询结果的第一条数据
           do {// 遍历Cursor对象，取出数据
               Classify classify=new Classify();
               classify.setId(cursor.getInt(cursor.getColumnIndex("id")));
               classify.setClassname(cursor.getString((cursor.getColumnIndex("classname"))));
               classlist.add(classify);
           } while (cursor.moveToNext());
       }
       cursor.close();
    }


    //适配数据
   public void classadaper(){
       final ClassifyAdapter adapter = new ClassifyAdapter(ModifyActivity.this,R.layout.classifyitem,classlist);
       ListView listView = findViewById(R.id.classlist);

       listView.setAdapter(adapter);

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long l) {
               final EditText et = new EditText(ModifyActivity.this);
               new AlertDialog.Builder(ModifyActivity.this).setTitle("请输入要修改的分组内容")
                       .setIcon(R.drawable.ic_format_list_bulleted_black_24dp)
                       .setView(et)
                       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                              Classify classify= classlist.get(position);
                                if(!"".equals(et.getText().toString())){
                                    //更新数据库
                                    ContentValues contentValues=new ContentValues();
                                    contentValues.put("classname",et.getText().toString());
                                    sqLiteDatabase.update("classify",contentValues,"id=?",new String[]{classify.getId()+""});
                                    contentValues.clear();
                                    contentValues.put("classify",et.getText().toString());
                                    sqLiteDatabase.update("memorandum",contentValues,"classify=?",new String[]{classify.getClassname()+""});
                                    //删除集合中数据
                                    classify.setClassname(et.getText().toString());
                                    //更新列表数据
                                    adapter.notifyDataSetChanged();
                                }
                           }
                       }).setNegativeButton("取消", null).show();
           }
       });
    }
}
