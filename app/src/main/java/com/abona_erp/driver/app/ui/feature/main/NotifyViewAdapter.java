package com.abona_erp.driver.app.ui.feature.main;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.Data;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
  
  private Data data;
  
  public interface OnItemClickListener {
    void onItemClick(Notify notify);
    void onMapClick(Notify notify);
  }
  
  public NotifyViewAdapter(Context ctx) {
    context = ctx;
    mInflater = LayoutInflater.from(ctx);
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
  
    holder.setIsRecyclable(false);
    
    // Read content of notify.
    String jsonText = notify.getData();
      
    ////////////////////////////////////////////////////////////////////////////////////////////////
  
    data = new Data();
    Gson gson = new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      .create();
    data = gson.fromJson(jsonText, Data.class);
    
    ////////////////////////////////////////////////////////////////////////////////////////////////

    synchronized (this) {
      SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
        Locale.getDefault());
      String dateTime = sdf.format(data.getTaskItem().getTaskDueDateFinish());
      holder.tvTaskFinish.setText(dateTime);
    }
      
    if (data.getTaskItem().getKundenName() != null)
      holder.tvCustomerName.setText(data.getTaskItem().getKundenName());
    holder.tvCustomerNo.setText(String.valueOf(data.getTaskItem().getKundenNr()));
    String orderNo = String.valueOf(data.getTaskItem().getOrderNo());
    String tmp = orderNo.substring(0, 4);
    tmp += "/";
    tmp += orderNo.substring(4, 6);
    tmp += "/";
    tmp += orderNo.substring(6);
    holder.tvOrderNo.setText(tmp);
      
    if (data.getTaskItem().getReferenceIdCustomer1() != null)
      holder.tvReference1.setText(data.getTaskItem().getReferenceIdCustomer1());
    if (data.getTaskItem().getReferenceIdCustomer2() != null)
      holder.tvReference2.setText(data.getTaskItem().getReferenceIdCustomer2());
    if (data.getTaskItem().getDescription() != null)
      holder.tvDescription.setText(data.getTaskItem().getDescription());
    if (data.getTaskItem().getAddress().getName1() != null)
      holder.tvName1.setText(data.getTaskItem().getAddress().getName1());
    if (data.getTaskItem().getAddress().getName2() != null)
      holder.tvName2.setText(data.getTaskItem().getAddress().getName2());
    if (data.getTaskItem().getAddress().getStreet() != null)
      holder.tvStreet.setText(data.getTaskItem().getAddress().getStreet());
    if (data.getTaskItem().getAddress().getNation() != null)
      holder.tvNation.setText(data.getTaskItem().getAddress().getNation());
    if (data.getTaskItem().getAddress().getZip() != null)
      holder.tvZip.setText(data.getTaskItem().getAddress().getZip());
    if (data.getTaskItem().getAddress().getCity() != null)
      holder.tvCity.setText(data.getTaskItem().getAddress().getCity());
      
    if (notify.getRead()) {
      holder.livLabel.setVisibility(View.GONE);
    }
    
    holder.bind(notify, data.getTaskItem().getTaskDueDateFinish(), listener);
  }
  
  public void setOnItemListener(OnItemClickListener listener) {
    this.listener = listener;
  }
  
  public void clearAll() {
    handler.removeCallbacksAndMessages(null);
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
    
    public AsapTextView tvTaskFinish;
    
    public AsapTextView tvDueIn;
    
    public AsapTextView tvCustomerName;
    public AsapTextView tvCustomerNo;
    public AsapTextView tvOrderNo;
    public AsapTextView tvReference1;
    public AsapTextView tvReference2;
    public AsapTextView tvDescription;
    public AsapTextView tvName1;
    public AsapTextView tvName2;
    public AsapTextView tvStreet;
    public AsapTextView tvNation;
    public AsapTextView tvZip;
    public AsapTextView tvCity;
    public Button btnMap;
    
    public LabelImageView livLabel;
    
    public AppCompatImageView ivWarning;
    public LinearLayout llHeaderBackground;
    
    long dueInMillis;
    DueInCounterRunnable dueInCounter;
    
    public ViewHolder(View view) {
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
