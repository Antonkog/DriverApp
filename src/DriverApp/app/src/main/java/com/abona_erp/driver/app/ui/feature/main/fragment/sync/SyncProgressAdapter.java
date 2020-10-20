package com.abona_erp.driver.app.ui.feature.main.fragment.sync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.ChangeHistory;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.util.AppUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SyncProgressAdapter extends RecyclerView.Adapter<SyncProgressAdapter.MyViewHolder> {
    private final String TAG = "SyncProgressAdapter";
    ArrayList<OfflineConfirmation> offlineConfirmations = new ArrayList<>();

    public void swapData(List<OfflineConfirmation> logItems) {
        offlineConfirmations.clear();
        offlineConfirmations.addAll(logItems);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtWhen, txtWhat, txtTaskId, title, orderNo;
        public AppCompatImageView checkers;

        public MyViewHolder(View root) {
            super(root);
            txtWhat =  root.findViewById(R.id.text_what);
            txtWhen =  root.findViewById(R.id.text_when);
            title =  root.findViewById(R.id.text_title);
        }
    }

    public SyncProgressAdapter() {

    }

    @Override
    public SyncProgressAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {

        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sync_item, parent, false);
        MyViewHolder vh = new MyViewHolder(root);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        OfflineConfirmation current = offlineConfirmations.get(position);
        holder.txtWhen.setVisibility(View.GONE);
        holder.txtWhat.setText(ConfirmationType.getNameByOrdinal(holder.txtWhat.getContext(), current.getConfirmType()));
    }
    public final String formatUTCTZ(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return offlineConfirmations.size();
    }
}