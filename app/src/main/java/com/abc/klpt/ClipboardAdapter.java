package com.abc.klpt;

/**
 * Created by abhishekrai on 3/14/15.
 */

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClipboardAdapter extends RecyclerView.Adapter<ClipboardAdapter.ClipboardViewHolder> {

    private List<Clipboard> clipboardList;
    private Context context;
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

    public ClipboardAdapter(List<Clipboard> clipboardList, Context context) {
        this.clipboardList = clipboardList;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return clipboardList.size();
    }

    @Override
    public void onBindViewHolder(ClipboardViewHolder contactViewHolder, int i) {
        Clipboard ci = clipboardList.get(i);
        contactViewHolder.vCliptext.setText(ci.clipboardText);
        contactViewHolder.vTimestamp.setText(getFormattedDate(ci.timestamp));
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

        final ImageButton shareButton = (ImageButton)itemView.findViewById(R.id.share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView textView = (TextView) itemView.findViewById(R.id.clipText);
                String text = textView.getText().toString();

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,text);
                Intent new_intent = Intent.createChooser(sharingIntent, "Share via");
                new_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(new_intent);
            }
        });
        return new ClipboardViewHolder(itemView);
    }

    public static class ClipboardViewHolder extends RecyclerView.ViewHolder {
        protected TextView vCliptext;
        protected TextView vTimestamp;

        public ClipboardViewHolder(View v) {
            super(v);
            vCliptext = (TextView) v.findViewById(R.id.clipText);
            vTimestamp = (TextView) v.findViewById(R.id.timeStamp);
        }
    }
}
