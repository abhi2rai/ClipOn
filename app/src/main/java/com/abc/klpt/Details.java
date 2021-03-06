package com.abc.klpt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Details extends ActionBarActivity {

    EditText detailText;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.edit_icon);

        addButton = (Button)findViewById(R.id.saveButton);

        detailText = (EditText) findViewById(R.id.detailText);
        detailText.setMovementMethod(new ScrollingMovementMethod());

        ViewCompat.setTransitionName(getWindow().getDecorView().findViewById(android.R.id.content),"defaultAnimation");

        if(getIntent().hasExtra("clipboardText"))
        {
            detailText.setText(getIntent().getStringExtra("clipboardText"));
            if(getIntent().getStringExtra("clipboardText").length() < 12)
            {
                getSupportActionBar().setTitle("  " + getIntent().getStringExtra("clipboardText"));
            }else{
                getSupportActionBar().setTitle("  " + getIntent().getStringExtra("clipboardText").substring(0, 11) + "...");
            }
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }else{
            getSupportActionBar().setTitle("Add");
        }

        if(!getIntent().hasExtra("mode"))
        {
            addButton.setEnabled(false);
            addButton.setVisibility(View.INVISIBLE);
        }else if(getIntent().getStringExtra("mode").equals("edit"))
        {
            addButton.setText("SAVE");
        }
    }

    public void saveNewEntry(View v)
    {
        DbHandler db = new DbHandler(this);
        detailText = (EditText) findViewById(R.id.detailText);
        String text = detailText.getText().toString();
        if(text.equals("")) {
            Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show();
        }else if(!getIntent().getStringExtra("mode").equals("edit"))
        {
            db.addClipboardText(text);
            Intent intent = new Intent(this, MainActivity.class);
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeCustomAnimation(this, R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
            ActivityCompat.startActivity(this, intent, options.toBundle());
            Toast.makeText(this, "Added", Toast.LENGTH_LONG).show();
        }else if(getIntent().getStringExtra("mode").equals("edit")) {
            db.updateRecord(getIntent().getIntExtra("id", -1), text);
            Intent intent = new Intent(this, MainActivity.class);
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeCustomAnimation(this, R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
            ActivityCompat.startActivity(this, intent, options.toBundle());
            Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show();
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
                if(!text.equals(""))
                {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,text);
                    Intent new_intent = Intent.createChooser(sharingIntent, "Share via");
                    startActivity(new_intent);
                }
            }
        });
        return true;
    }
}
