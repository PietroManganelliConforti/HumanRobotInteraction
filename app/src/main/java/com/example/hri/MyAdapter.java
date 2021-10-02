package com.example.hri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyAdapter extends ArrayAdapter<String> {

    String [] names;
    int [] objects;
    Context mContext;

    public MyAdapter(@NonNull Context context, String[] objNames, int[] objs) {
        super(context, android.R.layout.simple_list_item_multiple_choice);
        this.names = objNames;
        this.objects = objs;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.listview_item, parent, false);
            mViewHolder.mObj = (ImageView) convertView.findViewById(R.id.imageView);
            mViewHolder.mName = (TextView) convertView.findViewById((R.id.textView));
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder)convertView.getTag();
        }
        mViewHolder.mObj.setImageResource(objects[position]);
        mViewHolder.mName.setText((names[position]));
        return convertView;
    }

    static class  ViewHolder{
        ImageView mObj;
        TextView mName;
    }
}
