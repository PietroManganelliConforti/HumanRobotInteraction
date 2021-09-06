package com.example.hri;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class ListOfObj extends AppCompatActivity {

    Toolbar mToolbar;
    ListView mListView;
    String[] objNames = {"Blackboard_1", "Desk_1", "Sofa_1", "Wardrobe_1"};

    List<String> selectedObjs = new ArrayList<String>();

    int[] objs = {
            R.drawable.blackboard,
            R.drawable.desk,
            R.drawable.sofa,
            R.drawable.wardrobe
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_obj);

        mToolbar=(Toolbar) findViewById((R.id.toolbar));
        mToolbar.setTitle((getResources().getString(R.string.app_name)));
        mListView=(ListView) findViewById((R.id.listview));
        MyAdapter myAdapter = new MyAdapter(ListOfObj.this,objNames,objs);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedObjs.add((String) objNames[i]);

            }
        });
    }


}