package com.example.hri;

import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfObj extends AppCompatActivity {

    int requestCode = MainActivity.requestCode;
    Toolbar mToolbar;
    ListView mListView;
    String[] objNames = {"Blackboard_1", "Desk_1", "Sofa_1", "Wardrobe_1"};
    Button okButton;
    boolean[] checkedItems = new boolean[objNames.length];
    int itemsSelectedCount=0;

    List<String> selectedObjs = new ArrayList<>();

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
        mToolbar= findViewById((R.id.toolbar));
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        mListView= findViewById((R.id.listview));
        okButton = findViewById((R.id.button));
        okButton.setOnClickListener(view -> closeAppOnTablet());
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        MyAdapter myAdapter = new MyAdapter(this,objNames,objs);
        mListView.setAdapter(myAdapter);

        mListView.setOnItemClickListener((adapterView, view, i, l) -> {

            checkedItems[i] = !checkedItems[i];
            if (checkedItems[i] && !Arrays.asList(selectedObjs).contains(objNames[i])){
                view.setBackgroundColor(Color.BLUE);
                selectedObjs.add(objNames[i]);
            }else{
                view.setBackgroundColor(Color.WHITE);
                selectedObjs.remove(objNames[i]);
            }
            //Log.i("My Activity", String.valueOf(selectedObjs));
        });
    }


    public List<String> getSelectedObjs(){
        return selectedObjs;
    }

    public void closeAppOnTablet() {
        finish();
    }


}