package com.example.memorandum;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.helper.MyDatabaseHelper;

public class AlarmActivity extends AppCompatActivity {
    String title1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        SharedPreferences sharedPreferences = getSharedPreferences("configuratioin", 0);
        String title = sharedPreferences.getString("title", "默认值");
        title1=title;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        //创建一个闹钟提醒的对话框,点击确定关闭铃声与页面
        new AlertDialog.Builder(AlarmActivity.this).setTitle("记得完成").setMessage(title + "")
                .setPositiveButton("关闭闹铃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmActivity.this.finish();
                        MyDatabaseHelper helper = new MyDatabaseHelper(AlarmActivity.this, "Memorandum.db", null, 1);
                        SQLiteDatabase db=helper.getWritableDatabase();
                        ContentValues contentValues=new ContentValues();
                        contentValues.put("status",1);
                        db.update("memorandum",contentValues,"title=?",new String[]{title1});
                        contentValues.clear();
                    }
                }).show();
    }
}
