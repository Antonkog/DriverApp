package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.logging.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private final String TAG = "MyAdapter";
    final static Pattern digitsPattern = Pattern.compile("\\d+");// Pattern.compile("[^0-9]+([0-9]+)$");
    ArrayList<LogItem> history;

    public void swapData(List<LogItem> logItems) {
        history.clear();
        history.addAll(logItems);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtWhen;
        public TextView txtWhat;
        public TextView txtId;

        public MyViewHolder(View root) {
            super(root);
            txtWhat = (TextView) root.findViewById(R.id.text_what);
            txtWhen = (TextView) root.findViewById(R.id.text_when);
            txtId = (TextView) root.findViewById(R.id.text_id);
        }
    }

    public HistoryAdapter() {
        history = new ArrayList<>();
    }

    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        MyViewHolder vh = new MyViewHolder(root);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.txtWhen.setText(formatUTCTZ(history.get(position).getCreatedAt()));
        String message = history.get(position).getMessage();
        try {
            Matcher matcher = digitsPattern.matcher(history.get(position).getMessage());
            if (matcher.find()) {
                holder.txtId.setText(matcher.group());
            }
        } catch (Exception e) {
            Log.i(TAG, "id not found at log message");
        }
        holder.txtWhat.setText(message.substring(0, message.length() - holder.txtId.getText().length()));
    }
    public final String formatUTCTZ(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return history.size();
    }
}