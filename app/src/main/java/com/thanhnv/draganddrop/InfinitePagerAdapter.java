package com.thanhnv.draganddrop;

import android.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanh on 8/10/2016.
 */
public class InfinitePagerAdapter{

    private List<Fragment> listData;

    public InfinitePagerAdapter(List<Fragment> listData){
        this.listData = listData;
    }

    public int getLength() {
        return listData.size();
    }

    public Fragment getFragmentPager(int curentPositionArrDataPager) {
        return listData.get(curentPositionArrDataPager);
    }
}
