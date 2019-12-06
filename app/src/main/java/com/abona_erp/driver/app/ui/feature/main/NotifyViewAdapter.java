package com.abona_erp.driver.app.ui.feature.main;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.TaskChangeReason;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.ui.widget.ProgressBarDrawable;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.flag_kit.FlagKit;
import com.lid.lib.LabelImageView;

import java.text.ParseException;
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
  
  private CommItem mCommItem = null;
  
  public interface OnItemClickListener {
    void onItemClick(Notify notify);
    void onMapClick(Notify notify);
    void onCameraClick(Notify notify);
  }
  
  public NotifyViewAdapter(Context ctx) {
    context = ctx;
    mInflater = LayoutInflater.from(ctx);
    
    mCommItem = new CommItem();
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
    mCommItem = App.getGson().fromJson(raw, CommItem.class);
  
    holder.setIsRecyclable(false);
    
    synchronized (this) {
      SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
        Locale.getDefault());
      if (mCommItem.getTaskItem().getTaskDueDateFinish() != null) {
        String dateTime = sdf.format(mCommItem.getTaskItem().getTaskDueDateFinish());
        holder.tvTaskFinish.setText(dateTime);
        holder.bind(notify, mCommItem.getTaskItem().getTaskDueDateFinish(), listener);
      }
    }
      
    if (mCommItem.getTaskItem().getKundenName() != null)
      holder.tvCustomerName.setText(mCommItem.getTaskItem().getKundenName());
    if (mCommItem.getTaskItem().getKundenNr() != null)
      holder.tvCustomerNo.setText(String.valueOf(mCommItem.getTaskItem().getKundenNr()));
    if (mCommItem.getTaskItem().getOrderNo() != null) {
      holder.tvOrderNo.setText(AppUtils.parseOrderNo(mCommItem.getTaskItem().getOrderNo()));
    }
    
    if (mCommItem.getTaskItem().getReferenceIdCustomer1() != null)
      holder.tvReference1.setText(mCommItem.getTaskItem().getReferenceIdCustomer1());
    if (mCommItem.getTaskItem().getReferenceIdCustomer2() != null)
      holder.tvReference2.setText(mCommItem.getTaskItem().getReferenceIdCustomer2());
    if (mCommItem.getTaskItem().getDescription() != null)
      holder.tvDescription.setText(mCommItem.getTaskItem().getDescription());
    if (mCommItem.getTaskItem().getAddress().getName1() != null)
      holder.tvName1.setText(mCommItem.getTaskItem().getAddress().getName1());
    if (mCommItem.getTaskItem().getAddress().getName2() != null)
      holder.tvName2.setText(mCommItem.getTaskItem().getAddress().getName2());
    if (mCommItem.getTaskItem().getAddress().getStreet() != null)
      holder.tvStreet.setText(mCommItem.getTaskItem().getAddress().getStreet());
    if (mCommItem.getTaskItem().getAddress().getNation() != null) {
      holder.tvNation.setText(mCommItem.getTaskItem().getAddress().getNation());
      holder.ivFlagKit.setCountryCode(mCommItem.getTaskItem().getAddress().getNation());
    }
    if (mCommItem.getTaskItem().getAddress().getZip() != null)
      holder.tvZip.setText(mCommItem.getTaskItem().getAddress().getZip());
    if (mCommItem.getTaskItem().getAddress().getCity() != null)
      holder.tvCity.setText(mCommItem.getTaskItem().getAddress().getCity());
    
    if (mCommItem.getTaskItem().getTaskId() > 0)
      holder.tvTaskId.setText(String.valueOf(mCommItem.getTaskItem().getTaskId()));
    
    if (mCommItem.getTaskItem().getActivities().size() > 0) {
      int size = mCommItem.getTaskItem().getActivities().size();
      if (size == 0) {
        holder.pbActivityStep.setVisibility(View.GONE);
        holder.tvPercent.setVisibility(View.GONE);
        holder.tvPercentStatus.setVisibility(View.GONE);
      } else {
        holder.pbActivityStep.setVisibility(View.VISIBLE);
        holder.tvPercent.setVisibility(View.VISIBLE);
        holder.tvPercentStatus.setVisibility(View.VISIBLE);
        ProgressBarDrawable progStep = new ProgressBarDrawable(size);
        holder.pbActivityStep.setProgressDrawable(progStep);
        int count = 0;
        for (int i = 0; i < size; i++) {
          String valid_until = "01/01/0001";
          SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
          Date strDate = null;
          try {
            strDate = sdf.parse(valid_until);
          } catch (ParseException e) {
            e.printStackTrace();
          }
          if (mCommItem.getTaskItem().getActivities().get(i).getStarted() != null && mCommItem.getTaskItem().getActivities().get(i).getStarted().after(strDate)) {
            count++;
            holder.tvPercentStatus.setText(mCommItem.getTaskItem().getActivities().get(i).getName());
          }
          if (mCommItem.getTaskItem().getActivities().get(i).getFinished() != null && mCommItem.getTaskItem().getActivities().get(i).getFinished().after(strDate))
            count++;
        }
        if (count <= 0) {
          holder.pbActivityStep.setProgress(0);
          holder.tvPercent.setText("0 %");
        } else if (count >= size*2) {
          holder.pbActivityStep.setProgress(100);
          holder.tvPercent.setText("100 %");
        } else {
          holder.pbActivityStep.setProgress(Math.round((100.f / (size*2)) * count));
          holder.tvPercent.setText(Math.round((100.f / (size*2)) * count) + " %");
        }
      }
    }
    
    if (mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.CREATED)) {
      if (notify.getRead()) {
        holder.livLabel.setVisibility(View.GONE);
      } else {
        holder.livLabel.setVisibility(View.VISIBLE);
        holder.livLabel.setLabelText(context.getResources().getString(R.string.label_new));
        holder.livLabel.setLabelBackgroundColor(context.getResources().getColor(R.color.clrLabelNew));
      }
    } else if (mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.UPDATED_ABONA)) {
      if (notify.getRead()) {
        holder.livLabel.setVisibility(View.GONE);
      } else {
        holder.livLabel.setVisibility(View.VISIBLE);
        holder.livLabel.setLabelText(context.getResources().getString(R.string.label_updated));
        holder.livLabel.setLabelBackgroundColor(context.getResources().getColor(R.color.clrLabelUpdated));
      }
    } else if (mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED)) {
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
    AppCompatButton btnMap;
    AppCompatButton btnCamera;
    
    LabelImageView livLabel;
    FlagKit ivFlagKit;
    
    AppCompatImageView ivWarning;
    LinearLayout llHeaderBackground;
    
    AsapTextView tvPercent;
    AsapTextView tvPercentStatus;
    ProgressBar pbActivityStep;
    
    // DEBUG
    AsapTextView tvTaskId;
    
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
      btnMap = (AppCompatButton) view.findViewById(R.id.btn_map);
      btnCamera = (AppCompatButton) view.findViewById(R.id.btn_camera);
      livLabel = (LabelImageView) view.findViewById(R.id.liv_label);
      ivWarning = (AppCompatImageView) view.findViewById(R.id.iv_warning_icon);
      llHeaderBackground = (LinearLayout) view.findViewById(R.id.ll_header_background);
      ivFlagKit = (FlagKit) view.findViewById(R.id.iv_flag_kit);
      pbActivityStep = (ProgressBar) view.findViewById(R.id.pb_activity_step);
      tvPercent = (AsapTextView) view.findViewById(R.id.tv_percent);
      tvPercentStatus = (AsapTextView) view.findViewById(R.id.tv_percent_status);
      tvTaskId = (AsapTextView) view.findViewById(R.id.tv_task_id);
  
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
      
      btnCamera.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onCameraClick(notify);
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
