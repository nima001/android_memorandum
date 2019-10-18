package com.example.memorandum;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.helper.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private MyDatabaseHelper dbhelper;
    private ArrayAdapter<String> arr_adapter;
    private List<String> data_list;
    private AlarmManager alarmManager;
    private PendingIntent pi;
    Spinner mspinner;
    SQLiteDatabase db;

    EditText madtitle = null;
    EditText mremarks = null;
    TextView mremind = null;
    String title = "";
    String remarks = "";


    int tagtitle = 0;
    int tagremarks = 0;
    int tagclassify = 0;

    int intentid;
    String intenttitle = null;
    String intentremarks = null;
    String classify = null;
    String date = null;

    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        c = Calendar.getInstance();

        //启用系统自带的导航栏返回键,AndroidManifest.xml里面配置父类activity，即可使用该按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbhelper = new MyDatabaseHelper(this, "Memorandum.db", null, 1);
        db = dbhelper.getWritableDatabase();

        madtitle = findViewById(R.id.adtitle);
        mremarks = findViewById(R.id.remarks);
        mremind = findViewById(R.id.remind);
        mspinner = findViewById(R.id.classifyspinner);
        data_list = new ArrayList<String>();

        Intent intent = getIntent();
        intenttitle = intent.getStringExtra("title");//取出intent携带的数据
        intentremarks = intent.getStringExtra("remarks");//取出intent携带的数据
        date = intent.getStringExtra("date");//取出intent携带的数据
        classify = intent.getStringExtra("classify");//取出intent携带的数据
        intentid = Integer.parseInt(intent.getStringExtra("id"));


//下拉框部分
        QueryClassify();
        data_list.add("新建");
        SpinnerView();
        //下拉框点击事件
        mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//通过此方法为下拉列表设置点击事件
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) {
                classify = mspinner.getItemAtPosition(i).toString();
                if ("新建".equals(classify)) {
                    final EditText et = new EditText(DetailActivity.this);
                    new AlertDialog.Builder(DetailActivity.this).setTitle("新建待办事项")
                            .setIcon(R.drawable.ic_format_list_bulleted_black_24dp)
                            .setView(et)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //按下确定键后的事件
                                    ContentValues values = new ContentValues();
                                    values.put("classname", et.getText().toString());
                                    db.insert("classify", null, values);
                                    values.clear();
                                    //清空数据
                                    data_list.clear();
                                    //查询数据库，找出更新后的所有分类名，放入datalist中
                                    QueryClassify();
                                    //设置固定数据"新建"
                                    data_list.add("新建");
                                    //将新建的分类显示在下拉框
                                    SpinnerView();
                                    mspinner.setSelection(arr_adapter.getPosition(et.getText().toString()));
                                }
                            }).setNegativeButton("取消", null).show();
                } else {
                    tagclassify++;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


//     修改时数据显示部分
        if (intenttitle != "") {
            madtitle.setText(intenttitle);
        }

        if (intentremarks != "") {
            mremarks.setText(intentremarks);
        } else {
            mremarks.setText("");
        }


        madtitle.addTextChangedListener(new TextWatcher() {
            //            文本改变前
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //            文本改变
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                title = madtitle.getText().toString();
                tagtitle++;
            }

            //            文本改变后
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        mremarks.addTextChangedListener(new TextWatcher() {
            //            文本改变前
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //            文本改变
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                remarks = mremarks.getText().toString();
                tagremarks++;
            }

            //            文本改变后
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//闹钟部分


        mremind.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("configuratioin", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent1 = new Intent(DetailActivity.this, AlarmActivity.class);
                if (title == "") {//标题没修改
                    editor.putString("title", madtitle.getText().toString());
                } else {//标题已修改
                    editor.putString("title", title);
                }
                editor.commit();

                pi = PendingIntent.getActivity(DetailActivity.this, 0, intent1, 0);
                Calendar currentTime = Calendar.getInstance();


                //不选择时间的时候，日期的时间会停留在上一次选择的日期上设置闹钟
                new TimePickerDialog(DetailActivity.this, 2,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                //获取Calendar这个类的实例：月份在Calendar中是从0开始的，也就是说1月份的值为0，因此需要加1才是现实中表示的月份
                                //                                c.setTimeInMillis(System.currentTimeMillis());
                                // 根据用户选择的时间来设置Calendar对象,设置完用c.getTimeInMillis()可以得到设置的时间
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.set(Calendar.SECOND, 0);
                                //设置AlarmManager在Calendar对应的时间启动service
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
                                // 提示闹钟设置完毕:
                                Toast.makeText(DetailActivity.this, "闹钟设置完毕~",
                                        Toast.LENGTH_SHORT).show();
                            }
                        },  // 设置初始时间
                        currentTime.get(Calendar.HOUR_OF_DAY), currentTime
                        .get(Calendar.MINUTE), false).show();



                new DatePickerDialog(DetailActivity.this, 2, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                        //设置当前时间
                        // 根据用户选择的时间来设置Calendar对象
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthofyear);
                        c.set(Calendar.DAY_OF_MONTH, dayofmonth);
                    }
                }, //设置初始日期
                        currentTime.get(Calendar.YEAR),
                        currentTime.get(Calendar.MONTH),
                        currentTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    //    显示下拉框内容
    public void SpinnerView() {
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        mspinner.setAdapter(arr_adapter);
        //显示ID对应的分组名
        mspinner.setSelection(arr_adapter.getPosition(classify));
    }

    //查询分组信息
    private void QueryClassify() {
        //数据
        data_list = new ArrayList<String>();
        Cursor cursor = db.query("classify", null, null, null, null, null, null);
        if ((cursor.moveToFirst())) {
            do {
                data_list.add(cursor.getString(cursor.getColumnIndex("classname")));
            } while ((cursor.moveToNext()));
        }
        cursor.close();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!madtitle.getText().toString().equals("") && !"新建".equals(classify)) {
            ContentValues values = new ContentValues();
            //文本改变，且是添加
            if (intentid == -1) {
                values.put("title", "" + title);
                values.put("remarks", "" + remarks);
                values.put("classify", "" + classify);
                values.put("status", "" + 0);
                db.insert("memorandum", null, values);
                values.clear();
            }
            //标题文本改变，且是修改
            if (tagtitle != 0 && intentid > 0) {
                values.put("title", title);
                values.put("status", "" + 0);
                db.update("memorandum", values, "id = ?", new String[]{String.valueOf(intentid)});
                values.clear();
            }

            //备注文本改变，且是修改
            if (tagremarks != 0 && intentid > 0) {
                values.put("remarks", "" + remarks);
                values.put("status", "" + 0);
                db.update("memorandum", values, "id = ?", new String[]{String.valueOf(intentid)});
                values.clear();
            }

            //分组改变，且是修改
            if (tagclassify != 0 && intentid > 0) {
                values.put("classify", "" + classify);
                values.put("status", "" + 0);
                db.update("memorandum", values, "id = ?", new String[]{String.valueOf(intentid)});
                values.clear();
            }
        }
        tagtitle = 0;
        tagremarks = 0;
        tagclassify = 0;
    }
}
