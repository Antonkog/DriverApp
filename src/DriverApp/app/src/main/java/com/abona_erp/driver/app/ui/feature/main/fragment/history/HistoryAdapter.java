package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.ChangeHistory;
import com.abona_erp.driver.app.util.AppUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private final String TAG = "HistoryAdapter";
    ArrayList<ChangeHistory> history;

    public void swapData(List<ChangeHistory> logItems) {
        history.clear();
        history.addAll(logItems);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtWhen, txtWhat, txtTaskId, title, orderNo;
        public AppCompatImageView checkers;

        public MyViewHolder(View root) {
            super(root);
            txtWhat = (TextView) root.findViewById(R.id.text_what);
            txtWhen = (TextView) root.findViewById(R.id.text_when);
            txtTaskId = (TextView) root.findViewById(R.id.txt_task_id);
            orderNo = (TextView) root.findViewById(R.id.txt_order_no);
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

        ChangeHistory  changeHistory = history.get(position);
        switch (changeHistory.getState()){
            case TO_BE_CONFIRMED_BY_APP:
                holder.checkers.setColorFilter(ContextCompat.getColor(holder.checkers.getContext(), R.color.clrConfirmationTypeReceived));
                break;

            case TO_BE_CONFIRMED_BY_DRIVER:
                holder.checkers.setColorFilter(ContextCompat.getColor(holder.checkers.getContext(), R.color.clrConfirmationTypeUser));
                break;

            case CONFIRMED:
                holder.checkers.setColorFilter(ContextCompat.getColor(holder.checkers.getContext(), R.color.clrConfirmationTypeAbona));
                break;
            default:
            case NO_SYNC:
                holder.checkers.setColorFilter(ContextCompat.getColor(holder.checkers.getContext(), R.color.smsp_transparent_color));
        }

        holder.txtWhen.setText(formatUTCTZ(history.get(position).getCreatedAt()));
        holder.txtWhat.setText(history.get(position).getMessage() + " " + history.get(position).getActionType().name());
        holder.title.setText(history.get(position).getTitle());

        if(history.get(position).getTaskId() > 0) holder.txtTaskId.setText(" "+history.get(position).getTaskId());
        else holder.txtTaskId.setText("");

        holder.orderNo.setText(AppUtils.parseOrderNo(history.get(position).getOrderNumber()));
    }
    public final String formatUTCTZ(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return history.size();
    }
}