package com.thanhnv.draganddrop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

/**
 * Created by thanh on 8/8/2016.
 */
public class MyPagerAdapter {
    private List<Integer> listIdLayout;
    private List<EventOfViewPagerInterFace> listEvent;
    private Context mContext;

    public MyPagerAdapter(Context context, List<Integer> listIdLayout, List<EventOfViewPagerInterFace> listEventOfViewInPager){
        this.listIdLayout = listIdLayout;
        mContext = context;
        listEvent = listEventOfViewInPager;
    }

    public int getCount(){
        return listIdLayout.size();
    }

    public View getViewPager(int position){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(listIdLayout.get(position), null, false);
        listEvent.get(position).setEventOfViewInPager(view);
        return view;
    }
}
