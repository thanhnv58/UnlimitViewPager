package com.thanhnv.draganddrop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by thanh on 8/5/2016.
 */
public class Pager {
    private View view;
    private OnEventOfChildrenViewInPager onEventOfChildrenViewInPager;
    public void setOnEventOfChildrenViewInPager(OnEventOfChildrenViewInPager event){
        onEventOfChildrenViewInPager = event;
        onEventOfChildrenViewInPager.setInitViews(view);
    }

    public Pager(Context context, int idLayout){
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(idLayout, null, false);

    }

    public View getView(){
        return view;
    }

    public interface OnEventOfChildrenViewInPager{
        void setInitViews(View view);
    }
}
