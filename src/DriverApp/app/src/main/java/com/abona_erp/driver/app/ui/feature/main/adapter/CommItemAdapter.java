package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.ActivityStep;
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
import com.abona_erp.driver.app.data.model.TaskStatus;
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
import com.abona_erp.driver.app.ui.widget.badges.Badge;
import com.abona_erp.driver.app.ui.widget.badges.BadgeSpan;
import com.abona_erp.driver.app.ui.widget.recyclerview.DoubleClickListener;
import com.abona_erp.driver.app.ui.widget.recyclerview.ExpandableItem;
import com.abona_erp.driver.app.ui.widget.recyclerview.ExpandableRecyclerView;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.flag_kit.FlagKit;
import com.lid.lib.LabelImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CommItemAdapter extends ExpandableRecyclerView.Adapter<Notify> {

  private CommItem mCommItem = null;

  private Resources _resources = ContextUtils.getApplicationContext().getResources();
  private Handler _handler = new Handler();
  private DueInCounterRunnable dueInCounter;
  
  private static int mCountProgressSteps = 0;
  
  // HEADER:
  private AppCompatImageView iv_confirmation;
  private AppCompatImageView iv_warning;
  private AsapTextView tv_task_finish;
  private AsapTextView tv_due_in;
  private AsapTextView tv_order_no;
  private AppCompatImageButton btn_contact_info;
  private AppCompatImageButton btn_notes_info;
  private AppCompatImageButton btn_dangerous_goods_info;
  private AppCompatImageButton btn_palette_info;
  private AppCompatImageButton btn_document_info;
  private AppCompatImageButton btn_task_history;
  private AppCompatImageButton btn_task_info;
  private LabelImageView status_label_view;
  
  // SUB HEADER:
  private FlagKit fk_flag;
  private AsapTextView tv_action_type;
  private AsapTextView tv_destination_address;
  private ProgressBar pb_activity_step;
  private AsapTextView tv_header_notes;
  private AsapTextView tv_header_notes_minus;
  private AsapTextView tv_activity_step_status;
  private AsapTextView tv_activity_step_status_message;
  private LinearLayout ll_sub_header;
  private AppCompatImageButton btn_camera;
  private AppCompatImageButton btn_map;
  
  // CONTENT:
  private RecyclerView rv_list;
  private ActivityStepAdapter mAdapter;
  private List<ActivityStep> mActivityList = new ArrayList<>();
  
  private ProgressBarDrawable mProgStep;
  //
  // NEW HEADER NEW HEADER NEW HEADER NEW HEADER NEW HEADER NEW HEADER NEW HEADER NEW HEADER NEW HEA
  //------------------------------------------------------------------------------------------------
  
  private AppCompatImageButton btn_go_process;
  private AppCompatButton btn_go_map;
  private AppCompatButton btn_go_camera;
  private AppCompatButton btn_go_document;
  
  private AppCompatImageView iv_dangerous_goods;
  private AppCompatImageView iv_palette_info;
  private AppCompatImageView iv_notes;
  private AppCompatImageView iv_contacts;
  
  
  
  private Map<Integer, Integer> badgeValues = new HashMap<>();
  private Map<Integer, Integer> uploadedDocumentBadgeValues = new HashMap<>();
  
  BadgeSpan badgeSpan;
  Badge badge;

  private final int clrPickUp = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrPickUp);
  private final int clrDropOff = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrDropOff);
  private final int clrGeneral = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrGeneral);
  private final int clrTractorSwap = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrTractorSwap);
  private final int clrDelay = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrDelay);
  private final int clrUnknown = ContextCompat.getColor(ContextUtils.getApplicationContext(), R.color.clrUnknown);
  
  SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
    Locale.getDefault());

  public CommItemAdapter(Context context, CommonItemClickListener listener) {
    super(context, listener);
    mCommItem = new CommItem();
    
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  
  @Override
  protected View createView(Context context, ViewGroup viewGroup, int viewType) {
    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.item_task_main, viewGroup, false);
    dueInCounter = new DueInCounterRunnable(_handler, context, tv_due_in, iv_warning, null, new Date());
    return view;
  }


  @Override
  @CallSuper
  public void onBindViewHolder(@NonNull final ExpandableRecyclerView.Adapter.ViewHolder holder, int position) {
    super.onBindViewHolder(holder, position);
    ExpandableItem expandableItem = (ExpandableItem) holder.getView(R.id.expandable_item);
    if(getItem(position).isCurrentlySelected()) expandableItem.showNow();
    else expandableItem.hideNow();
    
    if (App.selectedTaskPos == position) {
      expandableItem.showNow();
    } else {
      expandableItem.hideNow();
    }

    expandableItem.setOnClickListener(new DoubleClickListener() {
      @Override
      public void onSingleClick(View v) {
        if (expandableItem.isOpened()) expandableItem.hideNow(); else expandableItem.showNow();
        if (getListener() != null) getListener().onClick(v, position, getItem(position), !expandableItem.isOpened());
      }

      @Override
      public void onDoubleClick(View v) {
      }
    });
  }

  @Override
  protected void bindView(Notify item, ExpandableRecyclerView.Adapter.ViewHolder viewHolder) {
    if (item != null) {
      CommItem commItem = App.getInstance().gsonUtc.fromJson(item.getData(), CommItem.class);
      if (commItem == null) return;
      mCommItem = commItem;
      
      ExpandableItem expandableItem = (ExpandableItem)viewHolder.getView(R.id.expandable_item);
      if (expandableItem == null) return;
      if (expandableItem.getHeaderLayout() == null) return;
      if (expandableItem.getContentLayout() == null) return;
      
      // HEADER:
      iv_confirmation = (AppCompatImageView)expandableItem.getHeaderLayout().findViewById(R.id.iv_confirmation);
      iv_warning = (AppCompatImageView)expandableItem.getHeaderLayout().findViewById(R.id.iv_warning);
      tv_task_finish = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_task_finish);
      tv_due_in = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_due_in);
      tv_order_no = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_order_no);
      status_label_view = (LabelImageView)expandableItem.getHeaderLayout().findViewById(R.id.status_label_view);
      
      fk_flag = (FlagKit)expandableItem.getHeaderLayout().findViewById(R.id.fk_flag);
      tv_action_type = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_action_type);
      tv_destination_address = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_destination_address);
      pb_activity_step = (ProgressBar)expandableItem.getHeaderLayout().findViewById(R.id.pb_activity_step);
      tv_header_notes = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_header_notes);
      tv_header_notes_minus = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_header_notes_minus);
      tv_activity_step_status = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_activity_step_status);
      tv_activity_step_status_message = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_activity_step_status_message);
      ll_sub_header = (LinearLayout)expandableItem.getHeaderLayout().findViewById(R.id.ll_sub_header);
      btn_camera = (AppCompatImageButton)expandableItem.getHeaderLayout().findViewById(R.id.btn_camera);
      btn_map = (AppCompatImageButton)expandableItem.getHeaderLayout().findViewById(R.id.btn_map);
      
      rv_list = (RecyclerView)expandableItem.getContentLayout().findViewById(R.id.rv_list);
      LinearLayoutManager llm = new LinearLayoutManager(getContext(),
        RecyclerView.VERTICAL, false);
      rv_list.setLayoutManager(llm);
      
      updateConfirmationView(item);
      updateLabelView(item, mCommItem);
  
      if (commItem.getTaskItem().getActivities() != null) {
        int size = commItem.getTaskItem().getActivities().size();
        if (size > 0) {
          mProgStep = new ProgressBarDrawable(size);
          pb_activity_step.setVisibility(View.VISIBLE);
          pb_activity_step.setProgressDrawable(mProgStep);
      
          for (int i = 0; i < size; i++) {
            if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.PENDING)) {
              tv_activity_step_status.setText(getActivityStatusString(ActivityStatus.PENDING));
              tv_activity_step_status_message.setText(mCommItem.getTaskItem().getActivities().get(i).getName());
              break;
            }
            if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
              tv_activity_step_status.setText(getActivityStatusString(ActivityStatus.RUNNING));
              tv_activity_step_status_message.setText(mCommItem.getTaskItem().getActivities().get(i).getName());
              break;
            }
          }
          mCountProgressSteps = 0;
          for (int i = 0; i < size; i++) {
            if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
              mCountProgressSteps++;
            }
            if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
              mCountProgressSteps += 2;
            }
          }
          if (mCountProgressSteps == 0) {
            pb_activity_step.setVisibility(View.VISIBLE);
            pb_activity_step.setProgress(0);
          } else if (mCountProgressSteps == size * 2) {
            pb_activity_step.setVisibility(View.VISIBLE);
            pb_activity_step.setProgress(100);
          } else {
            pb_activity_step.setVisibility(View.VISIBLE);
            pb_activity_step.setProgress(Math.round((100.0f / (size * 2.0f)) * mCountProgressSteps));
          }
        }
      } else {
        pb_activity_step.setProgress(0);
        pb_activity_step.setVisibility(View.GONE);
      }
  
      btn_contact_info = (AppCompatImageButton)expandableItem.getHeaderLayout().findViewById(R.id.btn_contact_info);
      btn_contact_info.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (commItem.getTaskItem() != null && commItem.getTaskItem().getContacts() != null) {
            if (commItem.getTaskItem().getContacts().size() > 0) {
          
              ContactDataAdapter dataAdapter = new ContactDataAdapter(commItem.getTaskItem().getContacts());
              CustomContactDialog dialog = new CustomContactDialog((Activity)getContext(),
                dataAdapter, AppUtils.parseOrderNo(commItem.getTaskItem().getOrderNo()),
                getActionTypeColor(commItem.getTaskItem().getActionType()));
              dialog.show();
              dialog.setCanceledOnTouchOutside(false);
            }
          }
        }
      });
  
      btn_notes_info = (AppCompatImageButton)expandableItem.getHeaderLayout().findViewById(R.id.btn_message_info);
      btn_notes_info.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (commItem.getTaskItem() != null && commItem.getTaskItem().getNotes() != null) {
            if (commItem.getTaskItem().getNotes().size() > 0) {
          
              NotesDataAdapter dataAdapter = new NotesDataAdapter(commItem.getTaskItem().getNotes());
              CustomNotesDialog dialog = new CustomNotesDialog((Activity)getContext(),
                dataAdapter, AppUtils.parseOrderNo(commItem.getTaskItem().getOrderNo()),
                getActionTypeColor(commItem.getTaskItem().getActionType()));
              dialog.show();
              dialog.setCanceledOnTouchOutside(false);
            }
          }
        }
      });
  
      btn_dangerous_goods_info = (AppCompatImageButton)expandableItem.getHeaderLayout().findViewById(R.id.btn_dangerous_goods_info);
      btn_dangerous_goods_info.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (commItem.getTaskItem() != null && commItem.getTaskItem().getDangerousGoods() != null) {
            if (commItem.getTaskItem().getDangerousGoods().isGoodsDangerous()) {
              List<DangerousGoods> _items = new ArrayList<>();
              _items.add(commItem.getTaskItem().getDangerousGoods());
              DangerousGoodsDataAdapter dataAdapter = new DangerousGoodsDataAdapter(_items);
              CustomDangerousGoodsDialog dialog = new CustomDangerousGoodsDialog((Activity)getContext(),
                dataAdapter, AppUtils.parseOrderNo(commItem.getTaskItem().getOrderNo()),
                getActionTypeColor(commItem.getTaskItem().getActionType()));
              dialog.show();
              dialog.setCanceledOnTouchOutside(false);
            }
          }
        }
      });
  
      btn_palette_info = (AppCompatImageButton)expandableItem.getHeaderLayout().findViewById(R.id.btn_palette_info);
      btn_palette_info.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (commItem.getTaskItem() != null && commItem.getTaskItem().getPalletExchange() != null) {
            List<PalletExchange> _items = new ArrayList<>();
            _items.add(commItem.getTaskItem().getPalletExchange());
            PaletteDataAdapter dataAdapter = new PaletteDataAdapter(_items);
            CustomPaletteDialog dialog = new CustomPaletteDialog((Activity)getContext(),
              dataAdapter, AppUtils.parseOrderNo(commItem.getTaskItem().getOrderNo()),
              getActionTypeColor(commItem.getTaskItem().getActionType()));
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
          }
        }
      });
  
      btn_document_info = (AppCompatImageButton)expandableItem.getHeaderLayout().findViewById(R.id.btn_document_info);
      btn_document_info.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          App.eventBus.post(new PageEvent(new PageItemDescriptor(PageItemDescriptor.PAGE_DOCUMENT), item));
        }
      });
      
//      btn_task_history = (AppCompatImageButton)expandableItem.getHeaderLayout().findViewById(R.id.btn_task_history);
//      btn_task_history.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//          EventBus.getDefault().post(new HistoryClick());
//        }
//      });
      
      btn_task_info = (AppCompatImageButton)expandableItem.getHeaderLayout().findViewById(R.id.btn_task_info);
      btn_task_info.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
  
          CustomTaskInfoDialog dialog = new CustomTaskInfoDialog((Activity)getContext(), commItem, 0);
          dialog.show();
          dialog.setCanceledOnTouchOutside(false);
        }
      });
      
      btn_camera.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          getListener().onCameraClick(item);
        }
      });
      
      btn_map.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          getListener().onMapClick(item);
        }
      });
  
      // LOGIC LOGIC LOGIC
      synchronized (CommItemAdapter.this) {
        if (commItem.getTaskItem().getTaskDueDateFinish() != null) {
          tv_task_finish.setText(sdf.format(commItem.getTaskItem().getTaskDueDateFinish()));
          if (commItem.getTaskItem().getTaskStatus() != null) {
            if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING) || commItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
              enableDueInTimer(true, tv_due_in, iv_warning, commItem.getTaskItem().getTaskDueDateFinish());
            } else {
              enableDueInTimer(false, tv_due_in, iv_warning, null);
            }
          }
        }
      }
  
      if (commItem.getTaskItem().getOrderNo() != null) {
        tv_order_no.setText(AppUtils.parseOrderNo(commItem.getTaskItem().getOrderNo()));
      }
  
      if (commItem.getTaskItem().getActionType() != null) {
        ll_sub_header.setBackgroundColor(getActionTypeColor(commItem.getTaskItem().getActionType()));
        tv_action_type.setText(getActionTypeString(commItem.getTaskItem().getActionType()));
      }
      
      if (commItem.getTaskItem().getAddress() != null) {
        String nation;
        String zip;
        String city;
        String street;
        String lat_lng = " (";
        String address = "";
        
        if (commItem.getTaskItem().getAddress().getNation() != null) {
          nation = commItem.getTaskItem().getAddress().getNation();
          fk_flag.setCountryCode(nation);
          address += nation + " - ";
        }
        if (commItem.getTaskItem().getAddress().getZip() != null) {
          zip = commItem.getTaskItem().getAddress().getZip();
          address += zip + " ";
        }
        if (commItem.getTaskItem().getAddress().getCity() != null) {
          city = commItem.getTaskItem().getAddress().getCity();
          address += city + ", ";
        }
        if (commItem.getTaskItem().getAddress().getStreet() != null) {
          street = commItem.getTaskItem().getAddress().getStreet();
          address += street;
        }
        if (commItem.getTaskItem().getAddress().getLatitude() != null || commItem.getTaskItem().getAddress().getLongitude() != null) {
          if (commItem.getTaskItem().getAddress().getLatitude() != null) {
            lat_lng += commItem.getTaskItem().getAddress().getLatitude() + ",";
          } else {
            lat_lng += ",";
          }
          if (commItem.getTaskItem().getAddress().getLongitude() != null) {
            lat_lng += commItem.getTaskItem().getAddress().getLongitude() + ")";
          } else {
            lat_lng += ")";
          }
          address += lat_lng;
        }
  
        tv_destination_address.setText(address);
      }
/*
      if (mCommItem.getTaskItem().getActivities() != null) {
        int size = mCommItem.getTaskItem().getActivities().size();
        if (size > 0) {
          mProgStep = new ProgressBarDrawable(size);
          pb_activity_step.setVisibility(View.VISIBLE);
          pb_activity_step.setProgressDrawable(mProgStep);
          
          
          for (int i = 0; i < size; i++) {
            if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.PENDING)) {
              tv_activity_step_status.setText(getActivityStatusString(ActivityStatus.PENDING));
              tv_activity_step_status_message.setText(commItem.getTaskItem().getActivities().get(i).getName());
              break;
            }
            if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
              tv_activity_step_status.setText(getActivityStatusString(ActivityStatus.RUNNING));
              tv_activity_step_status_message.setText(commItem.getTaskItem().getActivities().get(i).getName());
              break;
            }
          }
          mCountProgressSteps = 0;
          for (int i = 0; i < size; i++) {
            if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
              mCountProgressSteps++;
            }
            if (mCommItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
              mCountProgressSteps += 2;
            }
          }
          if (mCountProgressSteps == 0) {
            pb_activity_step.setVisibility(View.VISIBLE);
            pb_activity_step.setProgress(0);
          } else if (mCountProgressSteps == size * 2) {
            pb_activity_step.setVisibility(View.VISIBLE);
            pb_activity_step.setProgress(100);
          } else {
            pb_activity_step.setVisibility(View.VISIBLE);
            pb_activity_step.setProgress(Math.round((100.0f / (size * 2.0f)) * mCountProgressSteps));
          }
        }
      } else {
        pb_activity_step.setProgress(0);
        pb_activity_step.setVisibility(View.GONE);
      }
*/
      applyDangerousGoods(commItem.getTaskItem().getDangerousGoods());
      applyPallets(commItem.getTaskItem().getPalletExchange());
      applyNotes(commItem.getTaskItem().getNotes());
      applyContacts(commItem.getTaskItem().getContacts());
      applyDocuments(commItem.getDocumentItem());
      
      // CONTENT:
      mAdapter = new ActivityStepAdapter(getContext(), item);
      mActivityList.clear();
      if (mCommItem.getTaskItem().getActivities().size() > 0) {
        for (int i = 0; i < mCommItem.getTaskItem().getActivities().size(); i++) {
          mActivityList.add(new ActivityStep(mCommItem.getTaskItem().getTaskStatus(), mCommItem.getTaskItem().getActionType(), mCommItem.getTaskItem().getActivities().get(i)));
        }
      }
      mAdapter.setActivityStepItems(mActivityList,
        mCommItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED) ? true : false);
      rv_list.setAdapter(mAdapter);
      

/*

      if (photoBadge > 0) {
        badgeSpan = new BadgeSpan(_resources.getColor(R.color.clrAbona), _resources.getColor(R.color.clrWhite), 25);
        badge = new Badge(photoBadge, badgeSpan);
        if (badge != null && badge.isActual()) {
          String badgeText = badge.getBadgeText();
          String badgeTitle = _resources.getString(R.string.camera);
      
          btn_go_camera.setText(badgeTitle + " " + badgeText, AppCompatButton.BufferType.SPANNABLE);
      
          Spannable spannable = (Spannable) btn_go_camera.getText();
          spannable.setSpan(badge.getSpan(), badgeTitle.length()+1, badgeTitle.length()+1 + badgeText.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          btn_go_camera.setText(spannable);
      
          badgeValues.put(0, badge.getNumber());
        }
      }
  
      ArrayList<String> documents = new ArrayList<>();
      documents = item.getDocumentUrls();
      if (documents != null) {
        if (documents.size() <= 0) return;
        Gson gson = new Gson();
        AppFileInterchangeItem[] appFileInterchangeItems = gson.fromJson(documents.get(0), AppFileInterchangeItem[].class);
        if (appFileInterchangeItems != null) {
          if (appFileInterchangeItems.length > 0) {
            int count = 0;
            for (int i = 0; i < appFileInterchangeItems.length; i++) {
              if (appFileInterchangeItems[i].getTaskId() == 0 || appFileInterchangeItems[i].getTaskId() == item.getTaskId()) {
                count++;
              }
            }
            if (count <= 0) return;
            btn_document_info.setVisibility(View.VISIBLE);
            BadgeSpan uploadBadgeSpan = new BadgeSpan(_resources.getColor(R.color.clrAbona),
              _resources.getColor(R.color.clrWhite), 25);
            Badge uploadBadge = new Badge(count, uploadBadgeSpan);
            if (uploadBadge != null && uploadBadge.isActual()) {
              String badgeText = uploadBadge.getBadgeText();
              String badgeTitle = _resources.getString(R.string.document);
              
              btn_go_document.setText(badgeTitle + " " + badgeText, AppCompatButton.BufferType.SPANNABLE);
  
              Spannable spannable = (Spannable)btn_go_document.getText();
              spannable.setSpan(uploadBadge.getSpan(), badgeTitle.length()+1, badgeTitle.length()+1 + badgeText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
              btn_go_document.setText(spannable);
  
              uploadedDocumentBadgeValues.put(0, uploadBadge.getNumber());
            }
          }
        }
      }*/
    }
  }
  
  private void applyDocuments(DocumentItem document) {
    btn_document_info.setVisibility(View.VISIBLE);
  }
  
  private void applyDangerousGoods(DangerousGoods dangerousGoods) {
    if (dangerousGoods == null) {
      btn_dangerous_goods_info.setVisibility(View.GONE);
      btn_dangerous_goods_info.setImageDrawable(null);
      return;
    }
    if (dangerousGoods.isGoodsDangerous() != null) {
      if (dangerousGoods.isGoodsDangerous()) {
        btn_dangerous_goods_info.setVisibility(View.VISIBLE);
        btn_dangerous_goods_info.setImageDrawable(null);
  
        if (dangerousGoods.getDangerousGoodsClassType() != null) {
          btn_dangerous_goods_info.setImageDrawable(getDangerousGoodsClass(dangerousGoods.getDangerousGoodsClassType()));
        }
      } else {
        btn_dangerous_goods_info.setVisibility(View.GONE);
        btn_dangerous_goods_info.setImageDrawable(null);
      }
    }
  }
  
  private void applyPallets(PalletExchange palletExchange) {
    if (palletExchange == null) {
      btn_palette_info.setVisibility(View.GONE);
      return;
    }
    if(palletExchange.getPalletExchangeType().equals(EnumPalletExchangeType.YES)) {
      btn_palette_info.setVisibility(View.VISIBLE);
    } else {
      btn_palette_info.setVisibility(View.GONE);
    }
  }
  
  private void applyNotes(List<NotesItem> notes) {
    if (notes == null || notes.size() <= 0) {
      btn_notes_info.setVisibility(View.GONE);
      return;
    }
    btn_notes_info.setVisibility(View.VISIBLE);
    
    String comment_text = _resources.getQuantityString(R.plurals.numberOfNotesAvailable, notes.size(), notes.size());
    
    for (int i = 0; i < notes.size(); i++) {
      if (notes.get(i).getNoteType().equals(EnumNoteType.HIGH)) {
        tv_header_notes_minus.setVisibility(View.VISIBLE);
        tv_header_notes.setVisibility(View.VISIBLE);
        tv_header_notes.setText(comment_text);
        break;
      }
    }
  }
  
  private void applyContacts(List<ContactItem> contacts) {
    if (contacts == null || contacts.size() <= 0) {
      btn_contact_info.setVisibility(View.GONE);
      return;
    }
    
    btn_contact_info.setVisibility(View.VISIBLE);
  }
  
  private void enableDueInTimer(boolean enable, AsapTextView dueIn, AppCompatImageView ivWarning, Date finishDate) {
    if (enable) {
      _handler.removeCallbacks(dueInCounter);
      dueInCounter.tv_DueIn = dueIn;
      dueInCounter.iv_Warning = ivWarning;
      dueInCounter.ll_Background = null;
      dueInCounter.endDate = finishDate;
      _handler.postDelayed(dueInCounter, 100);
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
  
      long diff = (finishCalendar.getTimeInMillis() - endTaskCalendar.getTimeInMillis()) / 1000 / 60;
      long hours = diff / 60;
  
      long days = hours / 24;
      String d = diff < 0 ? "-" : "";
      d += String.valueOf(Math.abs(days));
      dueInCounter.tv_DueIn.setText(d + "d " + String.format("%02d", Math.abs(hours % 24)) + "h " + String.format("%02d", Math.abs(diff % 60)) + "min");
  
      if (diff < 0) {
        dueInCounter.iv_Warning.setVisibility(View.VISIBLE);
        //dueInCounter.ll_Background.setBackground(context.getResources().getDrawable(R.drawable.warning_header_bg));
      } else {
        dueInCounter.iv_Warning.setVisibility(View.GONE);
        //dueInCounter.ll_Background.setBackground(context.getResources().getDrawable(R.drawable.header_bg));
      }
    }
  }
  
  private void updateConfirmationView(Notify item) {
    
    if (item == null) return;
    
    if (item.getConfirmationStatus() == 0) {  // CONFIRMATION RECEIVED.
      iv_confirmation.setColorFilter(ContextCompat.getColor(getContext(), R.color.clrConfirmationTypeReceived), PorterDuff.Mode.SRC_IN);
    } else if (item.getConfirmationStatus() == 1) { // CONFIRMATION BY DEVICE.
      iv_confirmation.setColorFilter(ContextCompat.getColor(getContext(), R.color.clrConfirmationTypeUser), PorterDuff.Mode.SRC_IN);
    } else if (item.getConfirmationStatus() == 2) { // CONFIRMATION BY USER.
      iv_confirmation.setColorFilter(ContextCompat.getColor(getContext(), R.color.clrConfirmationTypeAbona), PorterDuff.Mode.SRC_IN);
    }
  }
  
  private void updateLabelView(Notify item, CommItem commItem) {
  
    if (commItem.getTaskItem().getChangeReason().equals(TaskChangeReason.CREATED)) {
      if (item.getRead()) {
        status_label_view.setVisibility(View.GONE);
      } else {
        status_label_view.setVisibility(View.VISIBLE);
        status_label_view.setLabelText(_resources.getString(R.string.label_new));
        status_label_view.setLabelBackgroundColor(ContextCompat.getColor(getContext(), R.color.clrLabelNew));
      }
    } else if (commItem.getTaskItem().getChangeReason().equals(TaskChangeReason.UPDATED_ABONA)) {
      if (item.getRead()) {
        status_label_view.setVisibility(View.GONE);
      } else {
        status_label_view.setVisibility(View.VISIBLE);
        status_label_view.setLabelText(_resources.getString(R.string.label_updated));
        status_label_view.setLabelBackgroundColor(ContextCompat.getColor(getContext(), R.color.clrLabelUpdated));
      }
    } else if (commItem.getTaskItem().getChangeReason().equals(TaskChangeReason.DELETED)) {
      if (item.getRead()) {
        status_label_view.setVisibility(View.GONE);
      } else {
        status_label_view.setVisibility(View.VISIBLE);
        status_label_view.setLabelText(_resources.getString(R.string.label_deleted));
        status_label_view.setLabelBackgroundColor(ContextCompat.getColor(getContext(), R.color.clrLabelDeleted));
      }
    }
  }
  
  private String getActivityStatusString(ActivityStatus status) {
    switch (status) {
      case PENDING:  return _resources.getString(R.string.pending);
      case RUNNING:  return _resources.getString(R.string.running);
      case FINISHED: return _resources.getString(R.string.completed);
      default:
        return "";
    }
  }
  
  private String getActionTypeString(TaskActionType type) {
    switch (type) {
      case PICK_UP:      return _resources.getString(R.string.action_type_pick_up);
      case DROP_OFF:     return _resources.getString(R.string.action_type_drop_off);
      case GENERAL:      return _resources.getString(R.string.action_type_general);
      case TRACTOR_SWAP: return _resources.getString(R.string.action_type_tractor_swap);
      case DELAY:        return _resources.getString(R.string.action_type_delay);
      case UNKNOWN:
      default:
        return _resources.getString(R.string.action_type_unknown);
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
  
  private Drawable getDangerousGoodsClass(DangerousGoodsClass dangerousGoodsClass) {
    switch (dangerousGoodsClass) {
      case CLASS_1_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives);
      case CLASS_1_1_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_1);
      case CLASS_1_2_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_2);
      case CLASS_1_3_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_3);
      case CLASS_1_4_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_4);
      case CLASS_1_5_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_5);
      case CLASS_1_6_EXPLOSIVES: return _resources.getDrawable(R.drawable.ic_class_1_explosives_1_6);
      case CLASS_2_FLAMMABLE_GAS: return _resources.getDrawable(R.drawable.ic_class_2_flammable_gas);
      case CLASS_2_NON_FLAMMABLE_GAS: return _resources.getDrawable(R.drawable.ic_class_2_non_flammable_gas);
      case CLASS_2_POISON_GAS: return _resources.getDrawable(R.drawable.ic_class_2_poison_gas);
      case CLASS_3_FLAMMABLE_LIQUID: return _resources.getDrawable(R.drawable.ic_class_3_flammable_liquid);
      case CLASS_4_1_FLAMMABLE_SOLIDS: return _resources.getDrawable(R.drawable.ic_class_4_flammable_solid);
      case CLASS_4_2_SPONTANEOUSLY_COMBUSTIBLE: return _resources.getDrawable(R.drawable.ic_class_4_spontaneously_combustible);
      case CLASS_4_3_DANGEROUSE_WHEN_WET: return _resources.getDrawable(R.drawable.ic_class_4_dangerous_when_wet);
      case CLASS_5_1_OXIDIZER: return _resources.getDrawable(R.drawable.ic_class_5_1_oxidizer);
      case CLASS_5_2_ORAGNIC_PEROXIDES: return _resources.getDrawable(R.drawable.ic_class_5_2_organic_peroxides);
      case CLASS_6_1_POISON: return _resources.getDrawable(R.drawable.ic_class_6_poison);
      case CLASS_6_2_INFECTIOUS_SUBSTANCE: return _resources.getDrawable(R.drawable.ic_class_6_2_infectious_substance);
      case CLASS_7_FISSILE: return _resources.getDrawable(R.drawable.ic_class_7_fissile);
      case CLASS_7_RADIOACTIVE_I: return _resources.getDrawable(R.drawable.ic_class_7_radioactive_i);
      case CLASS_7_RADIOACTIVE_II: return _resources.getDrawable(R.drawable.ic_class_7_radioactive_ii);
      case CLASS_7_RADIOACTIVE_III: return _resources.getDrawable(R.drawable.ic_class_7_radioactive_iii);
      case CLASS_8_CORROSIVE: return _resources.getDrawable(R.drawable.ic_class_8_corrosive);
      case CLASS_9_MISCELLANEOUS: return _resources.getDrawable(R.drawable.ic_class_9_miscellaneus);
      default: return _resources.getDrawable(R.drawable.ic_risk);
    }
  }
}
