package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ActivityItem;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.ActivityStep;
import com.abona_erp.driver.app.data.model.AddressItem;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ContactItem;
import com.abona_erp.driver.app.data.model.DangerousGoods;
import com.abona_erp.driver.app.data.model.DangerousGoodsClass;
import com.abona_erp.driver.app.data.model.DocumentItem;
import com.abona_erp.driver.app.data.model.EnumNoteType;
import com.abona_erp.driver.app.data.model.EnumPalletExchangeType;
import com.abona_erp.driver.app.data.model.NotesItem;
import com.abona_erp.driver.app.data.model.PalletExchange;
import com.abona_erp.driver.app.data.model.TaskActionType;
import com.abona_erp.driver.app.data.model.TaskChangeReason;
import com.abona_erp.driver.app.data.model.TaskItem;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.ui.event.HistoryClick;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.DueInCounterRunnable;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.ui.widget.ContactDataAdapter;
import com.abona_erp.driver.app.ui.widget.CustomContactDialog;
import com.abona_erp.driver.app.ui.widget.CustomDangerousGoodsDialog;
import com.abona_erp.driver.app.ui.widget.CustomNotesDialog;
import com.abona_erp.driver.app.ui.widget.CustomPaletteDialog;
import com.abona_erp.driver.app.ui.widget.CustomTaskInfoDialog;
import com.abona_erp.driver.app.ui.widget.DangerousGoodsDataAdapter;
import com.abona_erp.driver.app.ui.widget.NotesDataAdapter;
import com.abona_erp.driver.app.ui.widget.PaletteDataAdapter;
import com.abona_erp.driver.app.ui.widget.ProgressBarDrawable;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.flag_kit.FlagKit;
import com.lid.lib.LabelImageView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CommItemAdapterExt extends
  RecyclerView.Adapter<CommItemAdapterExt.ViewHolder> {
  
  private static final String TAG = CommItemAdapterExt.class.getSimpleName();
  
  private final Context mContext;
  private final Resources mResources;
  private final LayoutInflater mInflater;
  
  private Handler _handler = new Handler();
  private DueInCounterRunnable dueInCounter;
  public  AsapTextView tv_due_in;
  public AppCompatImageView iv_warning;
  
  private List<Notify> mDataList;
  private CommItem     mCommItem;
  
  private CommonItemClickListener<Notify> mListener;
  
  private List<ActivityStep> mActivityList = new ArrayList<>();
  
  private final int clrPickUp = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrPickUp);
  private final int clrDropOff = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrDropOff);
  private final int clrGeneral = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrGeneral);
  private final int clrTractorSwap = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrTractorSwap);
  private final int clrDelay = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrDelay);
  private final int clrUnknown = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrUnknown);
  
  SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
    Locale.getDefault());
  
  public CommonItemClickListener<Notify> getListener() {
    return mListener;
  }
  
  public CommItemAdapterExt(Context ctx, CommonItemClickListener listener) {
    mContext  = ctx;
    mListener = listener;
    mResources = mContext.getResources();
    mInflater = LayoutInflater.from(mContext);
    
    mCommItem = new CommItem();
    
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  
  @Override
  public void onBindViewHolder(CommItemAdapterExt.ViewHolder holder, int position) {
  
    final Notify notify = mDataList.get(position);
    if (notify == null) return;
    
    String rawJson = notify.getData();
    if (rawJson == null || TextUtils.isEmpty(rawJson)) return;
    mCommItem = App.getInstance().gsonUtc.fromJson(rawJson, CommItem.class);
    if (mCommItem == null) return;
    
    holder.setIsRecyclable(false);
  
    TaskItem taskItem = mCommItem.getTaskItem();
    if (taskItem == null) return;
  
    if (notify.isCurrentlySelected() && holder.root_content.getVisibility() != View.VISIBLE) {
      holder.root_content.setVisibility(View.VISIBLE);
    }
    holder.root_header.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (holder.root_content.getVisibility() == View.VISIBLE) {
          holder.root_content.setVisibility(View.GONE);
          getListener().onClick(view, position, notify, false);
        } else if (holder.root_content.getVisibility() == View.GONE) {
          holder.root_content.setVisibility(View.VISIBLE);
          getListener().onClick(view, position, notify, true);
        }
      }
    });
    
    holder.btn_contact_info.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (taskItem.getContacts() != null && taskItem.getContacts().size() > 0) {
          ContactDataAdapter dataAdapter = new ContactDataAdapter(taskItem.getContacts());
          CustomContactDialog dialog = new CustomContactDialog((Activity)mContext,
            dataAdapter, AppUtils.parseOrderNo(taskItem.getOrderNo()),
            getActionTypeColor(taskItem.getActionType()));
          dialog.show();
          dialog.setCanceledOnTouchOutside(false);
        }
      }
    });
    
    holder.btn_notes_info.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (taskItem.getNotes() != null && taskItem.getNotes().size() > 0) {
          NotesDataAdapter dataAdapter = new NotesDataAdapter(taskItem.getNotes());
          CustomNotesDialog dialog = new CustomNotesDialog((Activity)mContext,
            dataAdapter, AppUtils.parseOrderNo(taskItem.getOrderNo()),
            getActionTypeColor(taskItem.getActionType()));
          dialog.show();
          dialog.setCanceledOnTouchOutside(false);
        }
      }
    });
    
    holder.btn_dangerous_goods_info.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (taskItem.getDangerousGoods() != null && taskItem.getDangerousGoods().isGoodsDangerous()) {
          List<DangerousGoods> _items = new ArrayList<>();
          _items.add(taskItem.getDangerousGoods());
          DangerousGoodsDataAdapter dataAdapter = new DangerousGoodsDataAdapter(_items);
          CustomDangerousGoodsDialog dialog = new CustomDangerousGoodsDialog((Activity)mContext,
            dataAdapter, AppUtils.parseOrderNo(taskItem.getOrderNo()),
            getActionTypeColor(taskItem.getActionType()));
          dialog.show();
          dialog.setCanceledOnTouchOutside(false);
        }
      }
    });
    
    holder.btn_palette_info.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (taskItem.getPalletExchange() != null) {
          List<PalletExchange> _items = new ArrayList<>();
          _items.add(taskItem.getPalletExchange());
          PaletteDataAdapter dataAdapter = new PaletteDataAdapter(_items);
          CustomPaletteDialog dialog = new CustomPaletteDialog((Activity)mContext,
            dataAdapter, AppUtils.parseOrderNo(taskItem.getOrderNo()),
            getActionTypeColor(taskItem.getActionType()));
          dialog.show();
          dialog.setCanceledOnTouchOutside(false);
        }
      }
    });
    
    holder.btn_document_info.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_DOCUMENT), notify));
      }
    });
    
    holder.btn_task_history.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        EventBus.getDefault().post(new HistoryClick());
      }
    });
    
    holder.btn_task_info.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        CustomTaskInfoDialog dialog = new CustomTaskInfoDialog((Activity)mContext, mCommItem, 0);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
      }
    });
    
    holder.btn_camera.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getListener().onCameraClick(notify);
      }
    });
    
    holder.btn_map.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getListener().onMapClick(notify);
      }
    });
    
    mActivityList.clear();
    if (taskItem.getActivities() != null && taskItem.getActivities().size() > 0) {
      for (int i = 0; i < taskItem.getActivities().size(); i++) {
        mActivityList.add(new ActivityStep(taskItem.getTaskStatus(), taskItem.getActionType(), taskItem.getActivities().get(i)));
      }
    }
    holder.adapterExt.setDataList(notify, mActivityList);
    
    updateConfirmationView(holder, notify);
    updateLabelView(holder, notify, mCommItem);
    
    if (taskItem.getOrderNo() != null && taskItem.getMandantId() != null) {
      holder.tv_order_no.setText(AppUtils.parseOrderNo(taskItem.getOrderNo()) + " (" + taskItem.getMandantId() +")");
    }
    
    synchronized (CommItemAdapterExt.this) {
      if (taskItem.getTaskDueDateFinish() != null) {
        holder.tv_task_finish.setText(sdf.format(taskItem.getTaskDueDateFinish()));
        if (taskItem.getTaskStatus() != null) {
          if (taskItem.getTaskStatus().equals(TaskStatus.PENDING) || taskItem.getTaskStatus().equals(TaskStatus.RUNNING)) {
            enableDueInTimer(true, tv_due_in, iv_warning, taskItem.getTaskDueDateFinish());
          } else {
            enableDueInTimer(false, tv_due_in, iv_warning, taskItem.getTaskDueDateFinish());
          }
        }
      }
    }
    
    if (taskItem.getActionType() != null) {
      holder.ll_sub_header.setBackgroundColor(getActionTypeColor(taskItem.getActionType()));
      holder.tv_action_type.setText(getActionTypeString(taskItem.getActionType()));
    }
    
    if (taskItem.getAddress() != null) {
      String nation;
      String zip;
      String city;
      String street;
      String lat_lng = " (";
      String address = "";
      AddressItem addressItem = taskItem.getAddress();
      
      if (addressItem.getNation() != null) {
        nation = addressItem.getNation();
        holder.fk_flag.setCountryCode(nation);
        address += nation + " - ";
      }
      if (addressItem.getZip() != null) {
        zip = addressItem.getZip();
        address += zip + " ";
      }
      if (addressItem.getCity() != null) {
        city = addressItem.getCity();
        address += city + ", ";
      }
      if (addressItem.getStreet() != null) {
        street = addressItem.getStreet();
        address += street;
      }
      if (addressItem.getLatitude() != null || addressItem.getLongitude() != null) {
        if (addressItem.getLatitude() != null) {
          lat_lng += addressItem.getLatitude() + ",";
        } else {
          lat_lng += ",";
        }
        if (addressItem.getLongitude() != null) {
          lat_lng += addressItem.getLongitude() + ")";
        } else {
          lat_lng += ")";
        }
        address += lat_lng;
      }
      
      holder.tv_destination_address.setText(address);
    }
    
    if (taskItem.getActivities() != null) {
      int size = taskItem.getActivities().size();
      if (size > 0) {
        ProgressBarDrawable progStep = new ProgressBarDrawable(size);
        holder.pb_activity_step.setVisibility(View.VISIBLE);
        holder.pb_activity_step.setProgressDrawable(progStep);
        
        List<ActivityItem> items = taskItem.getActivities();
        int mCountProgressSteps;
        for (int i = 0; i < size; i++) {
          if (items.get(i).getStatus().equals(ActivityStatus.PENDING)) {
            holder.tv_activity_step_status.setText(getActivityStatusString(ActivityStatus.PENDING));
            holder.tv_activity_step_status_message.setText(items.get(i).getName());
            break;
          }
          if (items.get(i).getStatus().equals(ActivityStatus.RUNNING)) {
            holder.tv_activity_step_status.setText(getActivityStatusString(ActivityStatus.RUNNING));
            holder.tv_activity_step_status_message.setText(items.get(i).getName());
            break;
          }
        }
        mCountProgressSteps = 0;
        for (int i = 0; i < size; i++) {
          if (items.get(i).getStatus().equals(ActivityStatus.RUNNING)) {
            mCountProgressSteps++;
          }
          if (items.get(i).getStatus().equals(ActivityStatus.FINISHED)) {
            mCountProgressSteps += 2;
          }
        }
        if (mCountProgressSteps == 0) {
          holder.pb_activity_step.setProgress(0);
        } else if (mCountProgressSteps == size * 2) {
          holder.pb_activity_step.setProgress(100);
        } else {
          holder.pb_activity_step.setProgress(Math.round((100.0f / (size * 2.0f)) * mCountProgressSteps));
        }
      }
    } else {
      holder.pb_activity_step.setProgress(0);
      holder.pb_activity_step.setVisibility(View.GONE);
    }
  
    applyDangerousGoods(holder, taskItem.getDangerousGoods());
    applyPallets(holder, taskItem.getPalletExchange());
    applyNotes(holder, taskItem.getNotes());
    applyContacts(holder, taskItem.getContacts());
    applyDocuments(holder, mCommItem.getDocumentItem());
  }
  
  @Override
  public CommItemAdapterExt.ViewHolder
  onCreateViewHolder(ViewGroup parent, int viewType) {
    
    View itemView = mInflater.inflate(R.layout.step_activity_ext, parent, false);
    
    dueInCounter = new DueInCounterRunnable(_handler, mContext, tv_due_in, iv_warning, null, new Date());
    
    return new CommItemAdapterExt.ViewHolder(itemView);
  }
  
  public void setDataList(List<Notify> dataList) {
    mDataList = dataList;
    notifyDataSetChanged();
  }
  
  // getItemCount() is called many times, and when it is first called,
  // mDataList has not been updated (means initially, it's null,
  // and we can't return null).
  @Override
  public int getItemCount() {
    if (mDataList != null) {
      return mDataList.size();
    } else return 0;
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
    
    final AppCompatImageView iv_confirmation;
    final AsapTextView tv_task_finish;
    final AsapTextView tv_order_no;
    final LabelImageView status_label_view;
    final LinearLayout ll_sub_header;
    final AsapTextView tv_action_type;
    final FlagKit fk_flag;
    final AsapTextView tv_destination_address;
    final ProgressBar pb_activity_step;
    final AsapTextView tv_activity_step_status;
    final AsapTextView tv_activity_step_status_message;
    final RecyclerView rv_sub_list;
  
    final AsapTextView tv_header_notes;
    final AsapTextView tv_header_notes_minus;
    
    final LinearLayout root_header;
    final LinearLayout root_content;
  
    //final AppCompatImageView iv_dangerous_goods;
    //final AppCompatImageView iv_palette_info;
    //final AppCompatImageView iv_notes;
    //final AppCompatImageView iv_contacts;
  
    final AppCompatImageButton btn_contact_info;
    final AppCompatImageButton btn_notes_info;
    final AppCompatImageButton btn_dangerous_goods_info;
    final AppCompatImageButton btn_palette_info;
    final AppCompatImageButton btn_document_info;
    final AppCompatImageButton btn_task_history;
    final AppCompatImageButton btn_task_info;
  
    private AppCompatImageButton btn_camera;
    private AppCompatImageButton btn_map;
    
    CommItemSubAdapterExt adapterExt;
    
    public ViewHolder(View itemView) {
      super(itemView);
      
      iv_confirmation = (AppCompatImageView)itemView.findViewById(R.id.iv_confirmation);
      iv_warning = (AppCompatImageView)itemView.findViewById(R.id.iv_warning);
      tv_task_finish = (AsapTextView)itemView.findViewById(R.id.tv_task_finish);
      tv_due_in = (AsapTextView)itemView.findViewById(R.id.tv_due_in);
      tv_order_no = (AsapTextView)itemView.findViewById(R.id.tv_order_no);
      status_label_view = (LabelImageView)itemView.findViewById(R.id.status_label_view);
      ll_sub_header = (LinearLayout)itemView.findViewById(R.id.ll_sub_header);
      tv_action_type = (AsapTextView)itemView.findViewById(R.id.tv_action_type);
      fk_flag = (FlagKit)itemView.findViewById(R.id.fk_flag);
      tv_destination_address = (AsapTextView)itemView.findViewById(R.id.tv_destination_address);
      pb_activity_step = (ProgressBar)itemView.findViewById(R.id.pb_activity_step);
      tv_activity_step_status = (AsapTextView)itemView.findViewById(R.id.tv_activity_step_status);
      tv_activity_step_status_message = (AsapTextView)itemView.findViewById(R.id.tv_activity_step_status_message);
      root_header = (LinearLayout)itemView.findViewById(R.id.root_header);
      root_content = (LinearLayout)itemView.findViewById(R.id.root_content);
      rv_sub_list = (RecyclerView)itemView.findViewById(R.id.rv_sub_list);
      LinearLayoutManager llm = new LinearLayoutManager(mContext);
      rv_sub_list.setLayoutManager(llm);
      rv_sub_list.setNestedScrollingEnabled(true);
      adapterExt = new CommItemSubAdapterExt(mContext);
      rv_sub_list.setAdapter(adapterExt);
      tv_header_notes = (AsapTextView)itemView.findViewById(R.id.tv_header_notes);
      tv_header_notes_minus = (AsapTextView)itemView.findViewById(R.id.tv_header_notes_minus);
  
      btn_contact_info = (AppCompatImageButton)itemView.findViewById(R.id.btn_contact_info);
      btn_notes_info = (AppCompatImageButton)itemView.findViewById(R.id.btn_message_info);
      btn_dangerous_goods_info = (AppCompatImageButton)itemView.findViewById(R.id.btn_dangerous_goods_info);
      btn_palette_info = (AppCompatImageButton)itemView.findViewById(R.id.btn_palette_info);
      btn_document_info = (AppCompatImageButton)itemView.findViewById(R.id.btn_document_info);
      btn_task_history = (AppCompatImageButton)itemView.findViewById(R.id.btn_task_history);
      btn_task_info = (AppCompatImageButton)itemView.findViewById(R.id.btn_task_info);
  
      btn_camera = (AppCompatImageButton)itemView.findViewById(R.id.btn_camera);
      btn_map = (AppCompatImageButton)itemView.findViewById(R.id.btn_map);
    }
  }
  
  private void updateConfirmationView(ViewHolder holder, Notify item) {
    if (item == null) return;
    if (item.getConfirmationStatus() == 0) { // CONFIRMATION RECEIVED.
      holder.iv_confirmation.setColorFilter(ContextCompat.getColor(mContext,
        R.color.clrConfirmationTypeReceived), PorterDuff.Mode.SRC_IN);
    } else if (item.getConfirmationStatus() == 1) { // CONFIRMATION BY DEVICE.
      holder.iv_confirmation.setColorFilter(ContextCompat.getColor(mContext,
        R.color.clrConfirmationTypeUser), PorterDuff.Mode.SRC_IN);
    } else if (item.getConfirmationStatus() == 2) { // CONFIRMATION BY USER.
      holder.iv_confirmation.setColorFilter(ContextCompat.getColor(mContext,
        R.color.clrConfirmationTypeAbona), PorterDuff.Mode.SRC_IN);
    }
  }
  
  private void updateLabelView(ViewHolder holder, Notify item, CommItem commItem) {
    
    if (commItem.getTaskItem().getChangeReason().equals(TaskChangeReason.CREATED)) {
      if (item.getRead()) {
        holder.status_label_view.setVisibility(View.GONE);
      } else {
        holder.status_label_view.setVisibility(View.VISIBLE);
        holder.status_label_view.setLabelText(mResources.getString(R.string.label_new));
        holder.status_label_view.setLabelBackgroundColor(ContextCompat.getColor(mContext, R.color.clrLabelNew));
      }
    } else if (commItem.getTaskItem().getChangeReason().equals(TaskChangeReason.UPDATED_ABONA)) {
      if (item.getRead()) {
        holder.status_label_view.setVisibility(View.GONE);
      } else {
        holder.status_label_view.setVisibility(View.VISIBLE);
        holder.status_label_view.setLabelText(mResources.getString(R.string.label_updated));
        holder.status_label_view.setLabelBackgroundColor(ContextCompat.getColor(mContext, R.color.clrLabelUpdated));
      }
    } else if (commItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED)) {
      if (item.getRead()) {
        holder.status_label_view.setVisibility(View.GONE);
      } else {
        holder.status_label_view.setVisibility(View.VISIBLE);
        holder.status_label_view.setLabelText(mResources.getString(R.string.label_deleted));
        holder.status_label_view.setLabelBackgroundColor(ContextCompat.getColor(mContext, R.color.clrLabelDeleted));
      }
    }
  }
  
  private String getActionTypeString(TaskActionType type) {
    switch (type) {
      case PICK_UP:      return mResources.getString(R.string.action_type_pick_up);
      case DROP_OFF:     return mResources.getString(R.string.action_type_drop_off);
      case GENERAL:      return mResources.getString(R.string.action_type_general);
      case TRACTOR_SWAP: return mResources.getString(R.string.action_type_tractor_swap);
      case DELAY:        return mResources.getString(R.string.action_type_delay);
      case UNKNOWN:
      default:
        return mResources.getString(R.string.action_type_unknown);
    }
  }
  
  private int getActionTypeColor(TaskActionType type) {
    switch (type) {
      case PICK_UP:      return clrPickUp;
      case DROP_OFF:     return clrDropOff;
      case GENERAL:      return clrGeneral;
      case TRACTOR_SWAP: return clrTractorSwap;
      case DELAY:        return clrDelay;
      case UNKNOWN:
      default:
        return clrUnknown;
    }
  }
  
  private String getActivityStatusString(ActivityStatus status) {
    switch (status) {
      case PENDING:  return mResources.getString(R.string.pending);
      case RUNNING:  return mResources.getString(R.string.running);
      case FINISHED: return mResources.getString(R.string.completed);
      default:
        return "";
    }
  }
  
  @SuppressLint({"DefaultLocale", "SetTextI18n"})
  private void enableDueInTimer(boolean enable, AsapTextView dueIn, AppCompatImageView ivWarning, Date finishDate) {
    
    if (enable) {
      _handler.removeCallbacks(dueInCounter);
      dueInCounter.tv_DueIn = dueIn;
      dueInCounter.iv_Warning = ivWarning;
      dueInCounter.ll_Background = null;
      dueInCounter.endDate = finishDate;
      _handler.postDelayed(dueInCounter, 250);
    } else {
      _handler.removeCallbacks(dueInCounter);
      
      if (finishDate == null) return;
      if (mCommItem == null || mCommItem.getTaskItem() == null || mCommItem.getTaskItem().getActivities() == null) return;
      if (mCommItem.getTaskItem().getActivities().size() <= 0) return;
      int lastIdx = mCommItem.getTaskItem().getActivities().size() - 1;
      if (mCommItem.getTaskItem().getActivities().get(lastIdx).getFinished() == null) return;
      Calendar endTaskCalendar = Calendar.getInstance();
      endTaskCalendar.setTime(mCommItem.getTaskItem().getActivities().get(lastIdx).getFinished());
  
      Calendar finishCalendar = Calendar.getInstance();
      finishCalendar.setTime(finishDate);
  
      long diff = (finishCalendar.getTimeInMillis() - endTaskCalendar.getTimeInMillis() - TimeZone.getDefault().getOffset(System.currentTimeMillis())) / 1000 / 60;
      long hours = diff / 60;
  
      long days = hours / 24;
      String d = diff < 0 ? "-" : "";
      d += String.valueOf(Math.abs(days));
      dueIn.setText(d + "d " + String.format("%02d", Math.abs(hours % 24)) + "h " + String.format("%02d", Math.abs(diff % 60)) + "min");
  
      if (diff < 0) {
        ivWarning.setVisibility(View.VISIBLE);
      } else {
        ivWarning.setVisibility(View.GONE);
      }
    }
  }
  
  private void applyDangerousGoods(ViewHolder holder, DangerousGoods dangerousGoods) {
    if (dangerousGoods == null) {
      holder.btn_dangerous_goods_info.setVisibility(View.GONE);
      holder.btn_dangerous_goods_info.setImageDrawable(null);
      return;
    }
    if (dangerousGoods.isGoodsDangerous() != null) {
      if (dangerousGoods.isGoodsDangerous()) {
        holder.btn_dangerous_goods_info.setVisibility(View.VISIBLE);
        holder.btn_dangerous_goods_info.setImageDrawable(null);
        
        if (dangerousGoods.getDangerousGoodsClassType() != null) {
          holder.btn_dangerous_goods_info.setImageDrawable(getDangerousGoodsClass(dangerousGoods.getDangerousGoodsClassType()));
        }
      } else {
        holder.btn_dangerous_goods_info.setVisibility(View.GONE);
        holder.btn_dangerous_goods_info.setImageDrawable(null);
      }
    }
  }
  
  private void applyDocuments(ViewHolder holder, DocumentItem document) {
    holder.btn_document_info.setVisibility(View.VISIBLE);
  }
  
  private void applyPallets(ViewHolder holder, PalletExchange palletExchange) {
    if (palletExchange == null) {
      holder.btn_palette_info.setVisibility(View.GONE);
      return;
    }
    if(palletExchange.getPalletExchangeType().equals(EnumPalletExchangeType.YES)) {
      holder.btn_palette_info.setVisibility(View.VISIBLE);
    } else {
      holder.btn_palette_info.setVisibility(View.GONE);
    }
  }
  
  private void applyNotes(ViewHolder holder, List<NotesItem> notes) {
    if (notes == null || notes.size() <= 0) {
      holder.btn_notes_info.setVisibility(View.GONE);
      return;
    }
    holder.btn_notes_info.setVisibility(View.VISIBLE);
    
    String comment_text = mResources.getQuantityString(R.plurals.numberOfNotesAvailable, notes.size(), notes.size());
    
    for (int i = 0; i < notes.size(); i++) {
      if (notes.get(i).getNoteType().equals(EnumNoteType.HIGH)) {
        holder.tv_header_notes_minus.setVisibility(View.VISIBLE);
        holder.tv_header_notes.setVisibility(View.VISIBLE);
        holder.tv_header_notes.setText(comment_text);
        break;
      }
    }
  }
  
  private void applyContacts(ViewHolder holder, List<ContactItem> contacts) {
    if (contacts == null || contacts.size() <= 0) {
      holder.btn_contact_info.setVisibility(View.GONE);
      return;
    }
    
    holder.btn_contact_info.setVisibility(View.VISIBLE);
  }
  
  private Drawable getDangerousGoodsClass(DangerousGoodsClass dangerousGoodsClass) {
    switch (dangerousGoodsClass) {
      case CLASS_1_EXPLOSIVES: return mResources.getDrawable(R.drawable.ic_class_1_explosives);
      case CLASS_1_1_EXPLOSIVES: return mResources.getDrawable(R.drawable.ic_class_1_explosives_1_1);
      case CLASS_1_2_EXPLOSIVES: return mResources.getDrawable(R.drawable.ic_class_1_explosives_1_2);
      case CLASS_1_3_EXPLOSIVES: return mResources.getDrawable(R.drawable.ic_class_1_explosives_1_3);
      case CLASS_1_4_EXPLOSIVES: return mResources.getDrawable(R.drawable.ic_class_1_explosives_1_4);
      case CLASS_1_5_EXPLOSIVES: return mResources.getDrawable(R.drawable.ic_class_1_explosives_1_5);
      case CLASS_1_6_EXPLOSIVES: return mResources.getDrawable(R.drawable.ic_class_1_explosives_1_6);
      case CLASS_2_FLAMMABLE_GAS: return mResources.getDrawable(R.drawable.ic_class_2_flammable_gas);
      case CLASS_2_NON_FLAMMABLE_GAS: return mResources.getDrawable(R.drawable.ic_class_2_non_flammable_gas);
      case CLASS_2_POISON_GAS: return mResources.getDrawable(R.drawable.ic_class_2_poison_gas);
      case CLASS_3_FLAMMABLE_LIQUID: return mResources.getDrawable(R.drawable.ic_class_3_flammable_liquid);
      case CLASS_4_1_FLAMMABLE_SOLIDS: return mResources.getDrawable(R.drawable.ic_class_4_flammable_solid);
      case CLASS_4_2_SPONTANEOUSLY_COMBUSTIBLE: return mResources.getDrawable(R.drawable.ic_class_4_spontaneously_combustible);
      case CLASS_4_3_DANGEROUSE_WHEN_WET: return mResources.getDrawable(R.drawable.ic_class_4_dangerous_when_wet);
      case CLASS_5_1_OXIDIZER: return mResources.getDrawable(R.drawable.ic_class_5_1_oxidizer);
      case CLASS_5_2_ORAGNIC_PEROXIDES: return mResources.getDrawable(R.drawable.ic_class_5_2_organic_peroxides);
      case CLASS_6_1_POISON: return mResources.getDrawable(R.drawable.ic_class_6_poison);
      case CLASS_6_2_INFECTIOUS_SUBSTANCE: return mResources.getDrawable(R.drawable.ic_class_6_2_infectious_substance);
      case CLASS_7_FISSILE: return mResources.getDrawable(R.drawable.ic_class_7_fissile);
      case CLASS_7_RADIOACTIVE_I: return mResources.getDrawable(R.drawable.ic_class_7_radioactive_i);
      case CLASS_7_RADIOACTIVE_II: return mResources.getDrawable(R.drawable.ic_class_7_radioactive_ii);
      case CLASS_7_RADIOACTIVE_III: return mResources.getDrawable(R.drawable.ic_class_7_radioactive_iii);
      case CLASS_8_CORROSIVE: return mResources.getDrawable(R.drawable.ic_class_8_corrosive);
      case CLASS_9_MISCELLANEOUS: return mResources.getDrawable(R.drawable.ic_class_9_miscellaneus);
      default: return mResources.getDrawable(R.drawable.ic_risk);
    }
  }
}
