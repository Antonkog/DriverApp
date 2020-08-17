package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.entity.LogItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private final String TAG = "HistoryAdapter";
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
        public TextView title;
        public AppCompatImageView checkers;

        public MyViewHolder(View root) {
            super(root);
            txtWhat = (TextView) root.findViewById(R.id.text_what);
            txtWhen = (TextView) root.findViewById(R.id.text_when);
            txtId = (TextView) root.findViewById(R.id.text_id);
            title = (TextView) root.findViewById(R.id.text_title);
            checkers = (AppCompatImageView) root.findViewById(R.id.checkers);
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

        holder.checkers.setVisibility(View.INVISIBLE);

        if(history.get(position).getLevel() == LogLevel.ERROR){
            holder.checkers.setVisibility(View.VISIBLE);
            holder.checkers.setColorFilter(ContextCompat.getColor(holder.checkers.getContext(), R.color.clrLabelDeleted));
            holder.txtWhen.setTextColor(ContextCompat.getColor(holder.checkers.getContext(), R.color.clrLabelDeleted));
            holder.txtWhat.setTextColor(ContextCompat.getColor(holder.checkers.getContext(), R.color.clrLabelDeleted));
            holder.title.setTextColor(ContextCompat.getColor(holder.checkers.getContext(), R.color.clrLabelDeleted));
            holder.txtId.setTextColor(ContextCompat.getColor(holder.checkers.getContext(), R.color.clrLabelDeleted));
        } else {
            holder.txtWhen.setTextColor(ContextCompat.getColor(holder.checkers.getContext(), R.color.dark));
            holder.txtWhat.setTextColor(ContextCompat.getColor(holder.checkers.getContext(), R.color.dark));
            holder.title.setTextColor(ContextCompat.getColor(holder.checkers.getContext(), R.color.dark));
            holder.txtId.setTextColor(ContextCompat.getColor(holder.checkers.getContext(), R.color.dark));
            if(history.get(position).getType() == LogType.APP_TO_SERVER){
                holder.checkers.setColorFilter(ContextCompat.getColor(holder.checkers.getContext(), R.color.clrLabelChanged));
                holder.checkers.setVisibility(View.VISIBLE);
            }// R.color.clrLabelChanged, R.color.grey_40
            else if(history.get(position).getType() == LogType.SERVER_TO_APP){
                holder.checkers.setColorFilter(ContextCompat.getColor(holder.checkers.getContext(), R.color.clrTaskFinished));
                holder.checkers.setVisibility(View.VISIBLE);
            }
        }

        holder.txtWhen.setText(formatUTCTZ(history.get(position).getCreatedAt()));
        holder.txtWhat.setText(history.get(position).getMessage());
        holder.title.setText(history.get(position).getTitle());

        if(history.get(position).getTaskId() > 0) holder.txtId.setText(" "+history.get(position).getTaskId());
        else holder.txtId.setText("");
    }
    public final String formatUTCTZ(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return history.size();
    }
}