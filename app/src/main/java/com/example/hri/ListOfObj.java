package com.example.hri;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfObj extends AppCompatActivity {

    Toolbar mToolbar;
    ListView mListView;
    String[] objNames = {"Blackboard_1", "Desk_1", "Sofa_1", "Wardrobe_1"};
    Button okButton;

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
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAppOnTablet();
            }
        });
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        MyAdapter myAdapter = new MyAdapter(this,objNames,objs);
        mListView.setAdapter(myAdapter);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                if (b){
                    if (!Arrays.asList(selectedObjs).contains(objNames[i])) {
                        selectedObjs.add(objNames[i]);
                    }
                }else{
                    if (Arrays.asList(selectedObjs).contains(objNames[i])) {
                        selectedObjs.remove(objNames[i]);}
                }


            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                //int idx = menuItem.getItemId();
                menuItem.setChecked(!menuItem.isChecked());

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        //mListView.setOnItemClickListener((adapterView, view, i, l) -> {
        //    view.setSelected(!view.isSelected());
        //    if (view.isSelected()){
        //        if (!Arrays.asList(selectedObjs).contains(objNames[i])) {
        //            selectedObjs.add(objNames[i]);
        //            //adapterView.getItemAtPosition(i).
        //            //ListView item = (ListView) adapterView.getSelectedItem();
        //            //item.Select();
//
        //        }
        //    }else{
        //        if (Arrays.asList(selectedObjs).contains(objNames[i])) {
        //            selectedObjs.remove(objNames[i]);}
        //    }
//
        //});
    }

    public List<String> getSelectedObjs(){
        return selectedObjs;
    }

    public void closeAppOnTablet() {
        finish();
    }


}