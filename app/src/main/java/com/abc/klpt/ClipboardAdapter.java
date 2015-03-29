package com.abc.klpt;

/**
 * Created by abhishekrai on 3/14/15.
 */

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClipboardAdapter extends RecyclerView.Adapter<ClipboardAdapter.ClipboardViewHolder> {

    private List<Clipboard> clipboardList;
    private Context context;
    private Activity activity;

    private enum months
    {
        Jan,
        Feb,
        Mar,
        Apr,
        May,
        Jun,
        Jul,
        Aug,
        Sep,
        Oct,
        Nov,
        Dec
    }

    public ClipboardAdapter(List<Clipboard> clipboardList, Context context,Activity activity) {
        this.clipboardList = clipboardList;
        this.context = context;
        this.activity = activity;
    }


    @Override
    public int getItemCount() {
        return clipboardList.size();
    }

    @Override
    public void onBindViewHolder(final ClipboardViewHolder contactViewHolder, int i) {
        final Clipboard ci = clipboardList.get(i);
        contactViewHolder.vCliptext.setText(ci.getClipboardText());
        contactViewHolder.vTimestamp.setText(getFormattedDate(ci.getTimestamp()));
        contactViewHolder.vStarred.setChecked(ci.getStarred());

        if (Build.VERSION_CODES.LOLLIPOP != Build.VERSION.SDK_INT) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int verticalMargin = (int)(contactViewHolder.cardView.getMaxCardElevation() + (0.474 * contactViewHolder.cardView.getRadius()));
            int horizontalMargin = (int)((contactViewHolder.cardView.getMaxCardElevation()*1.5) + (0.474 * contactViewHolder.cardView.getRadius()));
            params.setMargins(-horizontalMargin, -verticalMargin, -horizontalMargin,
                    -verticalMargin);
            contactViewHolder.cardView.setLayoutParams(params);
        }

        contactViewHolder.vStarred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHandler db = new DbHandler(context);
                final int starred;
                if(!clipboardList.get(clipboardList.indexOf(ci)).getStarred()) {
                    starred = 1;
                    contactViewHolder.vStarred.setChecked(true);
                }
                else {
                    starred = 0;
                    contactViewHolder.vStarred.setChecked(false);
                }
                db.markAsStarred(ci.getId(),starred);
                clipboardList.get(clipboardList.indexOf(ci)).setStarred(starred == 1);
            }
        });

        contactViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, Details.class);
                intent.putExtra("clipboardText", ci.getClipboardText());
                intent.putExtra("mode", "edit");
                intent.putExtra("id", ci.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeCustomAnimation(context, R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                ActivityCompat.startActivity(activity, intent, options.toBundle());
            }
        });
    }

    private String getFormattedDate(String dateTime)
    {
        Date todaysDate = Calendar.getInstance().getTime();
        String date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(sdf.format(todaysDate).equals(dateTime.split(" ")[0]))
        {
            date = "Today";
        }else {
            String[] dateArray = dateTime.split(" ")[0].split("-");
            date = dateArray[2] + " " + months.values()[Integer.parseInt(dateArray[1]) - 1] + "," + dateArray[0].substring(2,4);
        }
        return date + "  " + dateTime.split(" ")[1].split(":")[0] + ":" + dateTime.split(" ")[1].split(":")[1];
    }

    @Override
    public ClipboardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.activity_card_layout, viewGroup, false);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView textView = (TextView) itemView.findViewById(R.id.clipText);
                String text = textView.getText().toString();
                //Stop Clipboard service
                Intent intent = new Intent(context, CBWatcherService.class);
                context.stopService(intent);

                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", text);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, "Copied to clipboard",
                        Toast.LENGTH_SHORT).show();

                //Start service
                context.startService(intent);
                return true;
            }
        });

        return new ClipboardViewHolder(itemView);
    }

    public static class ClipboardViewHolder extends RecyclerView.ViewHolder {
        protected TextView vCliptext;
        protected TextView vTimestamp;
        protected CheckBox vStarred;
        protected CardView cardView;

        public ClipboardViewHolder(View v) {
            super(v);
            vCliptext = (TextView) v.findViewById(R.id.clipText);
            vTimestamp = (TextView) v.findViewById(R.id.timeStamp);
            vStarred = (CheckBox) v.findViewById(R.id.starred);
            cardView = (CardView)v;
        }
    }
}
