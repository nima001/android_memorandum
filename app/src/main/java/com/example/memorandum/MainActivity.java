package com.example.memorandum;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.adapter.MemoAdapter;
import com.example.entity.Memorandum;
import com.example.helper.MyDatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MyDatabaseHelper dbhelper;
    private  List<Memorandum> memoList = new ArrayList<>();
    SQLiteDatabase db;
    Memorandum memorandum1, memorandum2;
    private List<String> data_list;
    MemoAdapter adapter;
    NavigationView navigationView;
    DrawerLayout mDrawerLayout;
    String classname;
    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbhelper = new MyDatabaseHelper(this, "Memorandum.db", null, 1);
        db = dbhelper.getWritableDatabase();

        //设置侧边栏目分组信息
        data_list = new ArrayList<String>();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        QueryClassify();
        //设置显示全部,编辑和新建
        navigationView.getMenu().add(1, 1, 1, "显示全部");
        navigationView.getMenu().add(2, 2, 2, "新建");
        navigationView.getMenu().add(3, 3, 3, "编辑");

        //设置新建，编辑和显示全部高亮选中
//        menuItem.setCheckable(true);//设置选项可选
        navigationView.getMenu().findItem(1).setChecked(true);
        navigationView.getMenu().findItem(2).setChecked(true);
        navigationView.getMenu().findItem(3).setChecked(true);

        //设置新建和显示全部左边图标
        navigationView.getMenu().findItem(1).setIcon(R.drawable.ic_list_background);
        navigationView.getMenu().findItem(2).setIcon(R.drawable.ic_add_black_24dp);
        navigationView.getMenu().findItem(3).setIcon(R.drawable.ic_mode_edit_black_24dp);
        //取消导航栏图标着色
        navigationView.setItemIconTintList(null);


        // 动态添加侧边栏分组信息，通过循环datalist来添加，并且给每个添加过的item设置点击事件
        i = 0;
        for (; i < data_list.size(); i++) {
            navigationView.getMenu().add(data_list.get(i));
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
                    classname = menuItem.getTitle().toString();
                    if (menuItem.getItemId() == 1) {//显示全部
                        initdata();
                    } else if (menuItem.getItemId() == 2) {//新建
                        final EditText et = new EditText(MainActivity.this);
                        new AlertDialog.Builder(MainActivity.this).setTitle("新建分组")
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
                                        //更新侧边栏
                                        data_list.add(et.getText().toString());
                                        navigationView.getMenu().add(data_list.get(data_list.size() - 1));
                                    }
                                }).setNegativeButton("取消", null).show();
                    } else if (menuItem.getItemId() == 3) {
                            Intent intent = new Intent(MainActivity.this, ModifyActivity.class);
                            startActivity(intent);
                    } else {
                        //将组名对应的备忘录信息显示出来
                        QuerydataByclassname();
                    }
                    mDrawerLayout.closeDrawers();//关闭侧边菜单栏
                    return false;
                }
            });
        }


        memorandum1 = new Memorandum();
        memorandum2 = new Memorandum();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", -1 + "");
                startActivity(intent);
            }
        });
        initdata();
    }


    public void initdata() {
        //查询数据库里面memorandum表中的所有内容
        memoList.clear();
        Cursor cursor = db.query("memorandum", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {//cursor.moveToFirst()指向查询结果的第一条数据
            do {// 遍历Cursor对象，取出数据
                Memorandum memorandum = new Memorandum();
                memorandum.setId(cursor.getInt(cursor.getColumnIndex("id")));
                memorandum.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                memorandum.setDate(cursor.getString(cursor.getColumnIndex("date")));
                memorandum.setClassify(cursor.getString(cursor.getColumnIndex("classify")));
                memorandum.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
                memorandum.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                memoList.add(memorandum);
            } while (cursor.moveToNext());
        }
        cursor.close();
        listandadapter();
    }


    //设置listview内容
    public void listandadapter() {
        adapter = new MemoAdapter(MainActivity.this, R.layout.memolistitem, memoList);
        ListView listView = findViewById(R.id.memorandumlist);

        listView.setAdapter(adapter);
        //注册上下文菜单
        this.registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                memorandum1 = memoList.get(position);//获取点击的那一列的数据对象
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", memorandum1.getId() + "");
                intent.putExtra("title", memorandum1.getTitle());
                intent.putExtra("date", memorandum1.getDate());
                intent.putExtra("remarks", memorandum1.getRemarks());
                intent.putExtra("status", memorandum1.getStatus());
                intent.putExtra("classify", memorandum1.getClassify());
                startActivity(intent);
            }
        });
    }

    //按照分类名查询ID
    public void QuerydataByclassname() {
        memoList.clear();
        Cursor cursor = db.query("memorandum", null, "classify=?", new String[]{classname}, null, null, null);
        if ((cursor.moveToFirst())) {
            do {
                Memorandum memorandum = new Memorandum();
                memorandum.setId(cursor.getInt(cursor.getColumnIndex("id")));
                memorandum.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                memorandum.setDate(cursor.getString(cursor.getColumnIndex("date")));
                memorandum.setClassify(cursor.getString(cursor.getColumnIndex("classify")));
                memorandum.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
                memorandum.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                memoList.add(memorandum);
            } while ((cursor.moveToNext()));
        }
        cursor.close();
        listandadapter();
    }

    //查询分组信息
    public void QueryClassify() {
        //数据
        Cursor cursor = db.query("classify", null, null, null, null, null, null);
        if ((cursor.moveToFirst())) {
            do {
                data_list.add(cursor.getString(cursor.getColumnIndex("classname")));
            } while ((cursor.moveToNext()));
        }
        cursor.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("操作");
        menu.add(1, 1, 1, "删除");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        //获取当前position
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        switch (item.getItemId()) {
            case 1:
                memorandum2 = memoList.get(position);
                db.delete("memorandum", "id=?", new String[]{memorandum2.getId() + ""});
                memoList.remove(position);
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    // 创建选项菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        memoList.clear();
        switch (item.getItemId()) {
            case R.id.complete:
                Cursor cursor=db.query("memorandum", null, "status=?", new String[]{1+""}, null, null, null);
                if ((cursor.moveToFirst())) {
                    do {
                        Memorandum memorandum = new Memorandum();
                        memorandum.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        memorandum.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                        memorandum.setDate(cursor.getString(cursor.getColumnIndex("date")));
                        memorandum.setClassify(cursor.getString(cursor.getColumnIndex("classify")));
                        memorandum.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
                        memorandum.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                        memoList.add(memorandum);
                    } while ((cursor.moveToNext()));
                }
                cursor.close();
                listandadapter();
                break;
            case R.id.incomplete:
                Cursor cursor1=db.query("memorandum", null, "status=?", new String[]{0+""}, null, null, null);
                if ((cursor1.moveToFirst())) {
                    do {
                        Memorandum memorandum = new Memorandum();
                        memorandum.setId(cursor1.getInt(cursor1.getColumnIndex("id")));
                        memorandum.setTitle(cursor1.getString(cursor1.getColumnIndex("title")));
                        memorandum.setDate(cursor1.getString(cursor1.getColumnIndex("date")));
                        memorandum.setClassify(cursor1.getString(cursor1.getColumnIndex("classify")));
                        memorandum.setRemarks(cursor1.getString(cursor1.getColumnIndex("remarks")));
                        memorandum.setStatus(cursor1.getInt(cursor1.getColumnIndex("status")));
                        memoList.add(memorandum);
                    } while ((cursor1.moveToNext()));
                }
                cursor1.close();
                listandadapter();
                break;
            case R.id.all:
                initdata();
                listandadapter();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//解决备忘主页弹出完成提醒后无法立即变灰
    @Override
    protected void onResume() {
        super.onResume();
        initdata();
    }
}
