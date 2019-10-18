package com.example.helper;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.memorandum.MainActivity;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    //创建备忘录表语句
    public static final String CREATE_MEMORANDUM = "create table memorandum("
            + "id integer primary key autoincrement,"
            + "title text," + "date date," + "classify text," + "remarks text," + "status text)";
    //创建备忘录分组表语句
    public static final String CREATE_CLASSIFY = "create table classify("
            + "id integer primary key autoincrement,"
            + "classname text)";

    private Context mContext;

    //重写构造方法
    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    //SQLiteOpenHelper是抽象类，MyDatabaseHelper不是抽象类，MyDatabaseHelper继承了SQLiteOpenHelper，
    //所以MyDatabaseHelper必须实现SQLiteOpenHelper里面的所有方法，即onCreate()和onUpgrade()
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MEMORANDUM);//创建备忘录表
        sqLiteDatabase.execSQL(CREATE_CLASSIFY);//创建分组表
        sqLiteDatabase.execSQL("insert into classify(classname) Values('未分类')");
        sqLiteDatabase.execSQL("insert into classify(classname) Values('工作')");
    }

    //重写父类方法
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
