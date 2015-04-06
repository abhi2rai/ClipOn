package com.abc.klpt;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    RecyclerView recyclerView;
    ClipboardAdapter ca;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    FloatingActionButton fab;
    SearchView searchView;
    int searchMode = 0;
    Drawer.Result result;
    Toolbar mToolbar;
    SwitchDrawerItem toggleDrawerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        mToolbar.setTitle("All");
        setSupportActionBar(mToolbar);

        AccountHeader.Result headerResult = new AccountHeader()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header_image)
                .withProfileImagesClickable(false)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withName("Clip On!").withEmail("Clipboard Manager").withIcon(getResources().getDrawable(R.mipmap.ic_header_icon))
                )
                .build();

        toggleDrawerItem = new SwitchDrawerItem().withName("Clipboard Service");
        result = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withDisplayBelowToolbar(true)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.START | Gravity.LEFT)
                .withAccountHeader(headerResult)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        searchView.onActionViewCollapsed();
                    }

                    @Override
                    public void onDrawerClosed(View view) {

                    }
                })
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("All").withIcon(R.mipmap.ic_all_items),
                        new PrimaryDrawerItem().withName("Starred").withIcon(R.mipmap.ic_starred_items),
                        new DividerDrawerItem(),
                        toggleDrawerItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        setListOnActivityStart(position);
                        searchMode = position;
                    }
                })
                .build();

        ViewCompat.setTransitionName(getWindow().getDecorView().findViewById(android.R.id.content), "defaultAnimation");
        checkFirstRun();
        try {
            sharedpreferences = getSharedPreferences("kltp", Context.MODE_PRIVATE);
            if(!sharedpreferences.contains("enable"))
            {
                editor = sharedpreferences.edit();
                startService(new Intent(this, CBWatcherService.class));
                editor.putBoolean("enable",true);
                editor.apply();
            }else if(sharedpreferences.getBoolean("enable",false))
            {
                boolean serviceActive = false;
                ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
                {
                    if ("com.abc.klpt.CBWatcherService".equals(service.service.getClassName()))
                    {
                        serviceActive = true;
                    }
                }
                if(!serviceActive)
                    startService(new Intent(this, CBWatcherService.class));
            }
        }catch (Exception ex){
            Log.e("Error while starting :",ex.getMessage());
        }

        if(sharedpreferences.contains("enable"))
        {
            if(sharedpreferences.getBoolean("enable",false))
            {
                toggleDrawerItem.setChecked(true);
            }
            else {
                toggleDrawerItem.setChecked(false);
            }
        }

        toggleDrawerItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sharedpreferences.edit();
                if(isChecked)
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


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        ca = new ClipboardAdapter(createList(), getApplicationContext(),MainActivity.this);
        recyclerView.setAdapter(ca);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);

        SwipeableRecyclerViewTouchListener swipeDeleteTouchListener =
                new SwipeableRecyclerViewTouchListener(
                        recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    showUndoBar(position,ca.getObjectAt(position));
                                    ca.removeFromList(position);
                                }
                                ca.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                onDismissedBySwipeLeft(recyclerView, reverseSortedPositions);
                            }
                        });
        recyclerView.addOnItemTouchListener(swipeDeleteTouchListener);
    }

    private void setListOnActivityStart(int position){
        if(position == 1)
        {
            mToolbar.setTitle("Starred");
            ca = new ClipboardAdapter(createPriorityList(), getApplicationContext(),MainActivity.this);
            recyclerView.setAdapter(ca);
        }else if(position == 0){
            mToolbar.setTitle("All");
            ca = new ClipboardAdapter(createList(), getApplicationContext(),MainActivity.this);
            recyclerView.setAdapter(ca);
        }
    }

    private void showUndoBar(final int position, final Clipboard obj)
    {
        SnackbarManager.show(
                Snackbar.with(getApplicationContext())
                        .text("1 deleted")
                        .actionLabel("UNDO")
                        .actionColor(getResources().getColor(R.color.accent))
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .eventListener(new EventListener() {
                            @Override
                            public void onShow(Snackbar snackbar) {
                                searchView.setVisibility(View.INVISIBLE);
                                fab.hide(true);
                            }

                            @Override
                            public void onShowByReplace(Snackbar snackbar) {
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                            }

                            @Override
                            public void onDismiss(Snackbar snackbar) {
                            }

                            @Override
                            public void onDismissByReplace(Snackbar snackbar) {
                            }

                            @Override
                            public void onDismissed(Snackbar snackbar) {
                                fab.show(true);
                                if(ca.getObjectAt(obj) == -1)
                                    ca.removeFromDb(obj);
                                searchView.setVisibility(View.VISIBLE);
                            }
                        })
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                ca.addToList(position,obj);
                            }
                        }), MainActivity.this);
    }

    private void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun){
            String str = "<p>To see your clipboard data - copy anything on your android device's clipboard" +
                    " or you can add your own data too by tapping on the floating + icon.</p>" +
                    "You can perform the following operations" +
                    " on your clipboard data:<br/>" +
                    "&#8226; Star mark your data<br/>" +
                    "&#8226; View your star marked data<br/>"+
                    "&#8226; Add your own data<br/>" +
                    "&#8226; String based search<br/>";

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Welcome!")
                    .setMessage(Html.fromHtml(str))
                    .setNeutralButton("OK", null).show();

            TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.CENTER);

            dialog.show();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    public void addNewEntry(View v)
    {
        Intent intent = new Intent(this, Details.class);
        intent.putExtra("mode", "add");
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, fab, "defaultAnimation");
        ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListOnActivityStart(result.getCurrentSelection());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ca = new ClipboardAdapter(getSearchString(newText, searchMode), getApplicationContext(),MainActivity.this);
                recyclerView.setAdapter(ca);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Clipboard> createList() {
        DbHandler db = new DbHandler(getApplicationContext());
        return db.getAllClipboard();
    }

    private List<Clipboard> getSearchString(String text,int option) {
        DbHandler db = new DbHandler(getApplicationContext());

        return db.getQuery(text,option);
    }

    private List<Clipboard> createPriorityList() {
        DbHandler db = new DbHandler(getApplicationContext());
        return db.getMarkedClips();
    }
}
