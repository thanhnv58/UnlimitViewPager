package com.thanhnv.draganddrop;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanh on 8/10/2016.
 */
public class MainActivity3 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3);

        List<Fragment> listData1 = new ArrayList<Fragment>();
        for (int i = 0; i < 4; i++){
            PagerFragment pager = new PagerFragment();
            pager.setKey("pager " + i);
            listData1.add(pager);
        }

        List<Fragment> listData2 = new ArrayList<Fragment>();
        for (int i = 0; i < 4; i++){
            PagerFragment pager = new PagerFragment();
            pager.setKey("pager " + i);
            listData2.add(pager);
        }

        List<Fragment> listData3 = new ArrayList<Fragment>();
        for (int i = 0; i < 4; i++){
            PagerFragment pager = new PagerFragment();
            pager.setKey("pager " + i);
            listData3.add(pager);
        }

        InfinitePagerAdapter adapter = new InfinitePagerAdapter(listData1);
        InfiniteViewPager pager = (InfiniteViewPager)findViewById(R.id.pager);
        pager.setNumberOfLoadedPager(4);
        pager.setInfiniteAdapter(adapter);

        InfinitePagerAdapter adapter2 = new InfinitePagerAdapter(listData2);
        InfiniteViewPager pager2 = (InfiniteViewPager)findViewById(R.id.pager2);
        pager2.setInfiniteAdapter(adapter2);

        InfinitePagerAdapter adapter3 = new InfinitePagerAdapter(listData3);
        InfiniteViewPager pager3 = (InfiniteViewPager)findViewById(R.id.pager3);
        pager3.setInfiniteAdapter(adapter3);
    }
}
