package com.abona_erp.driver.app.ui.feature.main;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.data.model.TaskChangeReason;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.util.AppUtils;
import com.lid.lib.LabelImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotifyViewAdapter extends RecyclerView.Adapter<NotifyViewAdapter.ViewHolder> {
  
  private static final String TAG = NotifyViewAdapter.class.getSimpleName();

  private final Context context;
  private final LayoutInflater mInflater;
  private List<Notify> mNotifyList;
  private OnItemClickListener listener;
  private Handler handler = new Handler();
  
  private Data mData = null;
  
  public interface OnItemClickListener {
    void onItemClick(Notify notify);
    void onMapClick(Notify notify);
  }
  
  public NotifyViewAdapter(Context ctx) {
    context = ctx;
    mInflater = LayoutInflater.from(ctx);
    
    mData = new Data();
  }
  
  @Override
  public NotifyViewAdapter.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {
    
    View itemView = mInflater.inflate(R.layout.task_item, parent, false);
    return new NotifyViewAdapter.ViewHolder(itemView);
  }
  
  @Override
  public void onBindViewHolder(NotifyViewAdapter.ViewHolder holder, int position) {

    final Notify notify = mNotifyList.get(position);
    if (notify == null)
      return;
    
    String raw = notify.getData();
    if (raw == null || TextUtils.isEmpty(raw))
      return;
    mData = App.getGson().fromJson(raw, Data.class);
  
    holder.setIsRecyclable(false);
    
    synchronized (this) {
      SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
        Locale.getDefault());
      if (mData.getTaskItem().getTaskDueDateFinish() != null) {
        String dateTime = sdf.format(mData.getTaskItem().getTaskDueDateFinish());
        holder.tvTaskFinish.setText(dateTime);
        holder.bind(notify, mData.getTaskItem().getTaskDueDateFinish(), listener);
      }
    }
      
    if (mData.getTaskItem().getKundenName() != null)
      holder.tvCustomerName.setText(mData.getTaskItem().getKundenName());
    if (mData.getTaskItem().getKundenNr() != null)
      holder.tvCustomerNo.setText(String.valueOf(mData.getTaskItem().getKundenNr()));
    if (mData.getTaskItem().getOrderNo() != null) {
      holder.tvOrderNo.setText(AppUtils.parseOrderNo(mData.getTaskItem().getOrderNo()));
    }
    
    if (mData.getTaskItem().getReferenceIdCustomer1() != null)
      holder.tvReference1.setText(mData.getTaskItem().getReferenceIdCustomer1());
    if (mData.getTaskItem().getReferenceIdCustomer2() != null)
      holder.tvReference2.setText(mData.getTaskItem().getReferenceIdCustomer2());
    if (mData.getTaskItem().getDescription() != null)
      holder.tvDescription.setText(mData.getTaskItem().getDescription());
    if (mData.getTaskItem().getAddress().getName1() != null)
      holder.tvName1.setText(mData.getTaskItem().getAddress().getName1());
    if (mData.getTaskItem().getAddress().getName2() != null)
      holder.tvName2.setText(mData.getTaskItem().getAddress().getName2());
    if (mData.getTaskItem().getAddress().getStreet() != null)
      holder.tvStreet.setText(mData.getTaskItem().getAddress().getStreet());
    if (mData.getTaskItem().getAddress().getNation() != null)
      holder.tvNation.setText(mData.getTaskItem().getAddress().getNation());
    if (mData.getTaskItem().getAddress().getZip() != null)
      holder.tvZip.setText(mData.getTaskItem().getAddress().getZip());
    if (mData.getTaskItem().getAddress().getCity() != null)
      holder.tvCity.setText(mData.getTaskItem().getAddress().getCity());
    
    if (mData.getTaskItem().getChangeReason().equals(TaskChangeReason.CREATED)) {
      if (notify.getRead()) {
        holder.livLabel.setVisibility(View.GONE);
      } else {
        holder.livLabel.setVisibility(View.VISIBLE);
        holder.livLabel.setLabelText(context.getResources().getString(R.string.label_new));
        holder.livLabel.setLabelBackgroundColor(context.getResources().getColor(R.color.clrLabelNew));
      }
    } else if (mData.getTaskItem().getChangeReason().equals(TaskChangeReason.UPDATED_ABONA)) {
      if (notify.getRead()) {
        holder.livLabel.setVisibility(View.GONE);
      } else {
        holder.livLabel.setVisibility(View.VISIBLE);
        holder.livLabel.setLabelText(context.getResources().getString(R.string.label_updated));
        holder.livLabel.setLabelBackgroundColor(context.getResources().getColor(R.color.clrLabelUpdated));
      }
    } else if (mData.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED)) {
      if (notify.getRead()) {
        holder.livLabel.setVisibility(View.GONE);
      } else {
        holder.livLabel.setVisibility(View.VISIBLE);
        holder.livLabel.setLabelText(context.getResources().getString(R.string.label_deleted));
        holder.livLabel.setLabelBackgroundColor(context.getResources().getColor(R.color.clrLabelDeleted));
      }
    }
  }
  
  public void setOnItemListener(OnItemClickListener listener) {
    this.listener = listener;
  }
  
  public void clearAll() {
    handler.removeCallbacksAndMessages(null);
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
    
    AsapTextView tvTaskFinish;
    
    AsapTextView tvDueIn;
    
    AsapTextView tvCustomerName;
    AsapTextView tvCustomerNo;
    AsapTextView tvOrderNo;
    AsapTextView tvReference1;
    AsapTextView tvReference2;
    AsapTextView tvDescription;
    AsapTextView tvName1;
    AsapTextView tvName2;
    AsapTextView tvStreet;
    AsapTextView tvNation;
    AsapTextView tvZip;
    AsapTextView tvCity;
    Button btnMap;
    
    LabelImageView livLabel;
    
    AppCompatImageView ivWarning;
    LinearLayout llHeaderBackground;
    
    long dueInMillis;
    DueInCounterRunnable dueInCounter;
    
    ViewHolder(View view) {
      super(view);
      tvTaskFinish = (AsapTextView) view.findViewById(R.id.tv_task_finish);
      tvDueIn = (AsapTextView) view.findViewById(R.id.tv_due_in);
      tvCustomerName = (AsapTextView) view.findViewById(R.id.tv_customer_name);
      tvCustomerNo = (AsapTextView) view.findViewById(R.id.tv_customer_no);
      tvOrderNo = (AsapTextView) view.findViewById(R.id.tv_order_no);
      tvReference1 = (AsapTextView) view.findViewById(R.id.tv_reference_1);
      tvReference2 = (AsapTextView) view.findViewById(R.id.tv_reference_2);
      tvDescription = (AsapTextView) view.findViewById(R.id.tv_description);
      tvName1 = (AsapTextView) view.findViewById(R.id.tv_name1);
      tvName2 = (AsapTextView) view.findViewById(R.id.tv_name2);
      tvStreet = (AsapTextView) view.findViewById(R.id.tv_street);
      tvNation = (AsapTextView) view.findViewById(R.id.tv_nation);
      tvZip = (AsapTextView) view.findViewById(R.id.tv_zip);
      tvCity = (AsapTextView) view.findViewById(R.id.tv_city);
      btnMap = (Button) view.findViewById(R.id.btn_map);
      livLabel = (LabelImageView) view.findViewById(R.id.liv_label);
      ivWarning = (AppCompatImageView) view.findViewById(R.id.iv_warning_icon);
      llHeaderBackground = (LinearLayout) view.findViewById(R.id.ll_header_background);
  
      dueInCounter = new DueInCounterRunnable(handler, context, tvDueIn, ivWarning, llHeaderBackground, new Date());
    }
    
    public void setMillisUntilFinished(long dueInMillis) {
      this.dueInMillis = dueInMillis;
    }
    
    public void bind(Notify notify, Date finishDate, final OnItemClickListener listener) {
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          listener.onItemClick(notify);
        }
      });
      
      btnMap.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onMapClick(notify);
        }
      });
      
      handler.removeCallbacks(dueInCounter);
      dueInCounter.tv_DueIn = tvDueIn;
      dueInCounter.iv_Warning = ivWarning;
      dueInCounter.ll_Background = llHeaderBackground;
      dueInCounter.endDate = finishDate;
      handler.postDelayed(dueInCounter, 100);
    }
  }

  public void setNotifyList(List<Notify> notifyList) {
    mNotifyList = notifyList;
    notifyDataSetChanged();
  }

  // getItemCount() is called many times, and when it is first called,
  // mNotifyList has not been updated (means initially, it's null,
  // and we can't return null).
  @Override
  public int getItemCount() {
    if (mNotifyList != null)
      return mNotifyList.size();
    else return 0;
  }
}
