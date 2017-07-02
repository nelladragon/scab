package com.nelladragon.scab;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.nelladragon.scab.store.StoreContract;
import com.nelladragon.scab.store.StoreMain;

public class ContractStoreDetailActivity extends AppCompatActivity {
    public static final String APP_TO_VIEW = "App To View";

    private StoreContract contract;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_store_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String contractIdStr = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            contractIdStr = extras.getString(APP_TO_VIEW);
        }

        StoreMain controller = StoreMain.getSingleInstance(getApplicationContext());
        this.contract = controller.getContract(contractIdStr);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(this.contract.getName());
        }




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                setResult(MainActivity.TAB_CONTRACTSTORE);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
