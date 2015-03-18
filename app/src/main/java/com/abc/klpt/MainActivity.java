package com.abc.klpt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    RecyclerView recyclerView;
    ClipboardAdapter ca;
    SwitchCompat serviceToggle;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences("kltp", Context.MODE_PRIVATE);
        if(!sharedpreferences.contains("enable"))
        {
            editor = sharedpreferences.edit();
            startService(new Intent(this, CBWatcherService.class));
            editor.putBoolean("enable",true);
            editor.apply();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        ca = new ClipboardAdapter(createList(), getApplicationContext());
        recyclerView.setAdapter(ca);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ca = new ClipboardAdapter(createList(), getApplicationContext());
        recyclerView.setAdapter(ca);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.myswitch);
        serviceToggle = (SwitchCompat)MenuItemCompat.getActionView(item).findViewById(R.id.switchForActionBar);
        if(sharedpreferences.contains("enable"))
        {
            if(sharedpreferences.getBoolean("enable",false))
            {
                serviceToggle.setChecked(true);
            }
            else {
                serviceToggle.setChecked(false);
            }
        }
        MenuItemCompat.getActionView(item).findViewById(R.id.switchForActionBar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedpreferences.edit();
                if(serviceToggle.isChecked())
                {
                    startService(new Intent(getApplicationContext(), CBWatcherService.class));
                    editor.putBoolean("enable",true);
                }else {
                    stopService(new Intent(getApplicationContext(), CBWatcherService.class));
                    editor.putBoolean("enable",false);
                }
                editor.apply();
            }
        });
        return true;
    }

    private List<Clipboard> createList() {
        DbHandler db = new DbHandler(getApplicationContext());
        return db.getAllClipboard();
    }
}
