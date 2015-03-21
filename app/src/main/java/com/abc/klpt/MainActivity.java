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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.melnykov.fab.FloatingActionButton;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    RecyclerView recyclerView;
    ClipboardAdapter ca;
    SwitchCompat serviceToggle;
    ToggleButton priorityToggle;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    FloatingActionButton fab;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.main_icon);
        getSupportActionBar().setTitle("  " + "Clip On!");


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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
    }

    public void addNewEntry(View v)
    {
        Intent intent = new Intent(this, Details.class);
        intent.putExtra("mode", "add");
        startActivity(intent);
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
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ca = new ClipboardAdapter(getSearchString(newText,priorityToggle.isChecked()), getApplicationContext());
                recyclerView.setAdapter(ca);
                return false;
            }
        });


        MenuItemCompat.getActionView(item).findViewById(R.id.switchForActionBar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedpreferences.edit();
                if(serviceToggle.isChecked())
                {
                    startService(new Intent(getApplicationContext(), CBWatcherService.class));
                    editor.putBoolean("enable", true);
                }else {
                    stopService(new Intent(getApplicationContext(), CBWatcherService.class));
                    editor.putBoolean("enable", false);
                }
                editor.apply();
            }
        });

        priorityToggle = (ToggleButton)MenuItemCompat.getActionView(item).findViewById(R.id.priorityToggle);
        MenuItemCompat.getActionView(item).findViewById(R.id.priorityToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(priorityToggle.isChecked())
                {
                    ca = new ClipboardAdapter(createPriorityList(), getApplicationContext());
                    recyclerView.setAdapter(ca);
                }else {
                    ca = new ClipboardAdapter(createList(), getApplicationContext());
                    recyclerView.setAdapter(ca);
                }
            }
        });
        return true;
    }

    private List<Clipboard> createList() {
        DbHandler db = new DbHandler(getApplicationContext());
        return db.getAllClipboard();
    }

    private List<Clipboard> getSearchString(String text,boolean starred) {
        DbHandler db = new DbHandler(getApplicationContext());
        int flag =0;
        if(starred)
            flag = 1;
        return db.getQuery(text,flag);
    }

    private List<Clipboard> createPriorityList() {
        DbHandler db = new DbHandler(getApplicationContext());
        return db.getMarkedClips();
    }
}
