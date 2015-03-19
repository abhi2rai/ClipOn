package com.abc.klpt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class Details extends ActionBarActivity {

    EditText detailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.edit_icon);

        detailText = (EditText) findViewById(R.id.detailText);
        detailText.setMovementMethod(new ScrollingMovementMethod());
        detailText.setText(getIntent().getStringExtra("clipboardText"));
        detailText.setSelection(detailText.getText().length());

        if(getIntent().getStringExtra("clipboardText").length() < 12)
        {
            getSupportActionBar().setTitle("  " + getIntent().getStringExtra("clipboardText"));
        }else{
            getSupportActionBar().setTitle("  " + getIntent().getStringExtra("clipboardText").substring(0, 11) + "...");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_share_menu, menu);
        final MenuItem item = menu.findItem(R.id.menu_item_share);
        MenuItemCompat.getActionView(item).findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailText = (EditText) findViewById(R.id.detailText);
                String text = detailText.getText().toString();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,text);
                Intent new_intent = Intent.createChooser(sharingIntent, "Share via");
                startActivity(new_intent);
            }
        });
        return true;
    }
}
