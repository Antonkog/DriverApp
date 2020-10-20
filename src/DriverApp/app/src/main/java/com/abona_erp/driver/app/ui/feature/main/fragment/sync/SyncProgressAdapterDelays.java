package com.abona_erp.driver.app.ui.feature.main.fragment.sync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.entity.OfflineDelayReasonEntity;
import com.abona_erp.driver.app.data.model.ConfirmationType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SyncProgressAdapterDelays extends RecyclerView.Adapter<SyncProgressAdapterDelays.MyViewHolder> {
    private final String TAG = "SyncProgressAdapterDelays";
    ArrayList<OfflineDelayReasonEntity> offlineDelays = new ArrayList<>();

    public void swapData(List<OfflineDelayReasonEntity> logItems) {
        offlineDelays.clear();
        offlineDelays.addAll(logItems);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtWhen, txtWhat, title;

        public MyViewHolder(View root) {
            super(root);
            txtWhat =  root.findViewById(R.id.text_what);
            txtWhen =  root.findViewById(R.id.text_when);
            title =  root.findViewById(R.id.text_title);
        }
    }

    public SyncProgressAdapterDelays() {

    }

    @Override
    public SyncProgressAdapterDelays.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {

        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sync_item, parent, false);
        MyViewHolder vh = new MyViewHolder(root);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String format = holder.title.getContext().getResources().getString(R.string.delay_minutes);

        int minutes = offlineDelays.get(position).getDelayInMinutes();

        holder.txtWhat.setText(String.format(format,minutes));

        holder.txtWhen.setText(formatUTCTZ(offlineDelays.get(position).getTimestamp()));
    }

    public final String formatUTCTZ(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return offlineDelays.size();
    }
}