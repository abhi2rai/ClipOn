package com.abc.klpt;

import android.content.Context;
import android.widget.CompoundButton;

/**
 * Created by abhishekrai on 3/17/15.
 */
public class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

    private int id;
    Context context;

    public CheckedChangeListener(int i,Context context){
        this.id = i;
        this.context = context;
    }

    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
        DbHandler db = new DbHandler(context);
        int starred;
        if(arg1)
            starred = 1;
        else
            starred = 0;
        db.markAsStarred(id,starred);
    }
}
