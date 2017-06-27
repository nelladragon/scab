// Copyright (C) 2017 Peter Robinson and the Smart Contract Application Browser contributors.
package com.nelladragon.scab;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AppStoreFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appstore, container, false);
        Context ctx = rootView.getContext();

        TableLayout tableLayout = (TableLayout)rootView.findViewById(R.id.tableLayout);




        TableRow tr1 = new TableRow(ctx);
        tr1.setLayoutParams(new TableLayout.LayoutParams( TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        TextView textview = new TextView(ctx);
        textview.setText("TODO");
//textview.getTextColors(R.color.)
        textview.setTextColor(Color.YELLOW);
        tr1.addView(textview);
        tableLayout.addView(tr1, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        return rootView;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}