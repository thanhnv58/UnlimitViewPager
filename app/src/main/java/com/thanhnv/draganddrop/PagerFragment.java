package com.thanhnv.draganddrop;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by thanh on 8/9/2016.
 */
public class PagerFragment extends Fragment {
    private String key = "null";

    public void setKey(String k){
        key = k;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.pager_2, container, false);
        ((Button)view.findViewById(R.id.btn_pager_2_click)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = ((EditText)view.findViewById(R.id.edt_pager_2)).getText().toString();
                ((TextView)view.findViewById(R.id.txt_pager_2_content)).setText(content);
            }
        });
        ((TextView)view.findViewById(R.id.txt_pager_2_content)).setText(key);
        return view;
    }
}
