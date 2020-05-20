package com.abona_erp.driver.app.ui.feature.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Spannable;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.model.ActivityStatus;
import com.abona_erp.driver.app.data.model.AppFileInterchangeItem;
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
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.data.model.UploadItem;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.PageEvent;
import com.abona_erp.driver.app.ui.feature.main.DueInCounterRunnable;
import com.abona_erp.driver.app.ui.feature.main.PageItemDescriptor;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.app.ui.widget.ContactDataAdapter;
import com.abona_erp.driver.app.ui.widget.CustomContactDialog;
import com.abona_erp.driver.app.ui.widget.CustomDangerousGoodsDialog;
import com.abona_erp.driver.app.ui.widget.CustomNotesDialog;
import com.abona_erp.driver.app.ui.widget.CustomPaletteDialog;
import com.abona_erp.driver.app.ui.widget.DangerousGoodsDataAdapter;
import com.abona_erp.driver.app.ui.widget.NotesDataAdapter;
import com.abona_erp.driver.app.ui.widget.PaletteDataAdapter;
import com.abona_erp.driver.app.ui.widget.ProgressBarDrawable;
import com.abona_erp.driver.app.ui.widget.badges.Badge;
import com.abona_erp.driver.app.ui.widget.badges.BadgeSpan;
import com.abona_erp.driver.app.ui.widget.recyclerview.ExpandableItem;
import com.abona_erp.driver.app.ui.widget.recyclerview.ExpandableRecyclerView;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.core.base.ContextUtils;
import com.abona_erp.driver.flag_kit.FlagKit;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommItemAdapter extends ExpandableRecyclerView.Adapter<Notify> {
  
  private OnItemClickListener listener;
  
  public interface OnItemClickListener {
    void onItemClick(Notify notify);
    void onMapClick(Notify notify);
    void onCameraClick(Notify notify);
    void onDocumentClick(Notify notify);
  }

  private Resources _resources = ContextUtils.getApplicationContext().getResources();
  private Handler _handler = new Handler();
  private DueInCounterRunnable dueInCounter;
  
  private AppCompatImageButton btn_go_process;
  private AppCompatButton btn_go_map;
  private AppCompatButton btn_go_camera;
  private AppCompatButton btn_go_document;
  
  private AsapTextView tv_due_in;
  private AsapTextView tv_info_reference;
  private AsapTextView tv_info_reference_title;
  private AsapTextView tv_loading_order;
  private AsapTextView tv_loading_order_title;
  private AsapTextView tv_ref_id_1;
  private AsapTextView tv_ref_id_1_title;
  private AsapTextView tv_ref_id_2;
  private AsapTextView tv_ref_id_2_title;
  private AsapTextView tv_adr_class;
  private AsapTextView tv_adr_class_title;
  private AsapTextView tv_un_no;
  private AsapTextView tv_un_no_title;
  private AsapTextView tv_number_of_paletts;
  private AsapTextView tv_number_of_paletts_title;
  private AsapTextView tv_dpl;
  private AsapTextView tv_dpl_title;
  private AsapTextView tv_contact_title;
  private AsapTextView tv_contact;
  private AsapTextView tv_notes;
  private AsapTextView tv_notes_title;
  private AsapTextView tv_header_notes;
  private AsapTextView tv_header_notes_minus;
  private AppCompatImageView iv_dangerous_goods;
  private AppCompatImageView iv_palette_info;
  private AppCompatImageView iv_notes;
  private AppCompatImageView iv_contacts;
  private AppCompatImageView iv_warning_icon;
  private AppCompatImageButton btn_dangerous_goods_info;
  private AppCompatImageButton btn_palette_info;
  private AppCompatImageButton btn_contact_info;
  private AppCompatImageButton btn_notes_info;
  private AppCompatImageButton btn_document_info;
  
  private LinearLayout ll_dangerous_goods;
  private LinearLayout ll_paletts;
  private LinearLayout ll_notes;
  private LinearLayout ll_contacts;
  
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

  public CommItemAdapter(Context context, LinearLayoutManager layout, OnViewHolderClick listener) {
    super(context, layout, listener);
  }
  
  @Override
  protected View createView(Context context, ViewGroup viewGroup, int viewType) {
    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.item_task_main, viewGroup, false);
    
    dueInCounter = new DueInCounterRunnable(_handler, context, tv_due_in, iv_warning_icon, null, new Date());
    
    return view;
  }
  
  @Override
  protected void bindView(Notify item, ExpandableRecyclerView.Adapter.ViewHolder viewHolder) {
    if (item != null) {
      CommItem commItem = App.getGsonUtc().fromJson(item.getData(), CommItem.class);
      if (commItem == null) return;
      
      ExpandableItem expandableItem = (ExpandableItem)viewHolder.getView(R.id.expandable_item);
      
      btn_go_process = (AppCompatImageButton)expandableItem.getContentLayout().findViewById(R.id.btn_go_process);
      btn_go_process.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onItemClick(item);
        }
      });
      btn_go_map = (AppCompatButton)expandableItem.getContentLayout().findViewById(R.id.btn_go_map);
      btn_go_map.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onMapClick(item);
        }
      });
      btn_go_camera = (AppCompatButton)expandableItem.getContentLayout().findViewById(R.id.btn_go_camera);
      btn_go_camera.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onCameraClick(item);
        }
      });
      btn_go_document = (AppCompatButton)expandableItem.getContentLayout().findViewById(R.id.btn_go_document);
      btn_go_document.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onDocumentClick(item);
        }
      });
      
      iv_warning_icon = (AppCompatImageView)expandableItem.getHeaderLayout().findViewById(R.id.iv_warning_icon);
      iv_dangerous_goods = (AppCompatImageView)expandableItem.getContentLayout().findViewById(R.id.iv_dangerous_goods);
      iv_palette_info = (AppCompatImageView)expandableItem.getContentLayout().findViewById(R.id.iv_palette_info);
      iv_contacts = (AppCompatImageView)expandableItem.getContentLayout().findViewById(R.id.iv_contacts);
      iv_notes = (AppCompatImageView)expandableItem.getContentLayout().findViewById(R.id.iv_notes);
      
      AsapTextView tv_task_finish = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_task_finish);
      tv_due_in = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_due_in);
      AsapTextView tv_order_no = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_order_no);
      AsapTextView tv_action_type = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_action_type);
      AsapTextView tv_destination_address = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_destination_address);
      AsapTextView tv_activity_step_status = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_activity_step_status);
      AsapTextView tv_activity_step_status_message = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_activity_step_status_message);
      AsapTextView tv_name = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_name);
      AsapTextView tv_geo_data = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_geo_data);
      AsapTextView tv_client = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_client);
      AsapTextView tv_client_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_client_title);
      tv_info_reference = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_info_reference);
      tv_info_reference_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_info_reference_title);
      tv_loading_order = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_loading_order);
      tv_loading_order_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_loading_order_title);
      tv_ref_id_1 = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_ref_id_1);
      tv_ref_id_1_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_ref_id_1_title);
      tv_ref_id_2 = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_ref_id_2);
      tv_ref_id_2_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_ref_id_2_title);
      tv_adr_class = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_adr_class);
      tv_adr_class_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_adr_class_title);
      tv_un_no = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_un_no);
      tv_un_no_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_un_no_title);
      tv_number_of_paletts = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_number_of_paletts);
      tv_number_of_paletts_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_number_of_paletts_title);
      tv_dpl = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_dpl);
      tv_dpl_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_dpl_title);
      tv_contact = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_contact);
      tv_contact_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_contact_title);
      tv_notes = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_notes);
      tv_notes_title = (AsapTextView)expandableItem.getContentLayout().findViewById(R.id.tv_notes_title);
      tv_header_notes = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_header_notes);
      tv_header_notes_minus = (AsapTextView)expandableItem.getHeaderLayout().findViewById(R.id.tv_header_notes_minus);
  
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
  
      ProgressBar pb_activity_step = (ProgressBar)expandableItem.getHeaderLayout().findViewById(R.id.pb_activity_step);;
  
      LinearLayout ll_sub_header = (LinearLayout)expandableItem.getHeaderLayout().findViewById(R.id.ll_sub_header);
      LinearLayout ll_sub_content = (LinearLayout)expandableItem.getContentLayout().findViewById(R.id.ll_sub_content);
      LinearLayout ll_sub_button_content = (LinearLayout)expandableItem.getContentLayout().findViewById(R.id.ll_sub_button_content);
      View view_gap = (View)expandableItem.getHeaderLayout().findViewById(R.id.view_gap);
      
      ll_dangerous_goods = (LinearLayout)expandableItem.getContentLayout().findViewById(R.id.ll_dangerous_goods);
      ll_dangerous_goods.setOnClickListener(new View.OnClickListener() {
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
      ll_paletts = (LinearLayout)expandableItem.getContentLayout().findViewById(R.id.ll_paletts);
      ll_paletts.setOnClickListener(new View.OnClickListener() {
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
      ll_notes = (LinearLayout)expandableItem.getContentLayout().findViewById(R.id.ll_notes);
      ll_notes.setOnClickListener(new View.OnClickListener() {
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
      ll_contacts = (LinearLayout)expandableItem.getContentLayout().findViewById(R.id.ll_contacts);
      ll_contacts.setOnClickListener(new View.OnClickListener() {
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
  
      FlagKit fk_flag = (FlagKit)expandableItem.getHeaderLayout().findViewById(R.id.fk_flag);
      
      // LOGIC LOGIC LOGIC
      synchronized (CommItemAdapter.this) {
        if (commItem.getTaskItem().getTaskDueDateFinish() != null) {
          tv_task_finish.setText(sdf.format(commItem.getTaskItem().getTaskDueDateFinish()));
          if (commItem.getTaskItem().getTaskStatus() != null) {
            if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING) || commItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
              enableDueInTimer(true, tv_due_in, iv_warning_icon, commItem.getTaskItem().getTaskDueDateFinish());
            } else {
              enableDueInTimer(false, tv_due_in, iv_warning_icon, null);
            }
          }
        }
      }
      
      if (commItem.getTaskItem().getOrderNo() != null) {
        tv_order_no.setText(AppUtils.parseOrderNo(commItem.getTaskItem().getOrderNo()));
      }
      if (commItem.getTaskItem().getActionType() != null) {
        ll_sub_header.setBackgroundColor(getActionTypeColor(commItem.getTaskItem().getActionType()));
        ll_sub_content.setBackgroundColor(getActionTypeColor(commItem.getTaskItem().getActionType()));
        ll_sub_button_content.setBackgroundColor(getActionTypeColor(commItem.getTaskItem().getActionType()));
        tv_action_type.setText(getActionTypeString(commItem.getTaskItem().getActionType()));
        if (expandableItem.isOpened()) {
          view_gap.setBackgroundColor(getActionTypeColor(commItem.getTaskItem().getActionType()));
        } else {
          view_gap.setBackgroundColor(Color.TRANSPARENT);
        }
      }
      if (commItem.getTaskItem().getAddress() != null) {
        String nation;
        String zip;
        String city;
        String street;
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
        
        tv_destination_address.setText(address);
        if (commItem.getTaskItem().getAddress().getName1() != null || commItem.getTaskItem().getAddress().getName2() != null) {
          tv_name.setVisibility(View.VISIBLE);
          String name = "";
          if (commItem.getTaskItem().getAddress().getName1() != null) {
            name += commItem.getTaskItem().getAddress().getName1();
          }
          if (commItem.getTaskItem().getAddress().getName2() != null) {
            name += " (" + commItem.getTaskItem().getAddress().getName2() + ")";
          }
          tv_name.setText(name);
        } else {
          tv_name.setVisibility(View.GONE);
        }
        if (commItem.getTaskItem().getAddress().getLatitude() != null || commItem.getTaskItem().getAddress().getLongitude() != null) {
          tv_geo_data.setVisibility(View.VISIBLE);
          String lat_lng = "(lat: ";
          if (commItem.getTaskItem().getAddress().getLatitude() != null) {
            lat_lng += commItem.getTaskItem().getAddress().getLatitude() + ", lng: ";
          } else {
            lat_lng += ", lng: ";
          }
          if (commItem.getTaskItem().getAddress().getLongitude() != null) {
            lat_lng += commItem.getTaskItem().getAddress().getLongitude() + ")";
          } else {
            lat_lng += ")";
          }
          tv_geo_data.setText(lat_lng);
        } else {
          tv_geo_data.setVisibility(View.GONE);
        }
      }
      if (commItem.getTaskItem().getActivities() != null) {
        int size = commItem.getTaskItem().getActivities().size();
        if (size > 0) {
          pb_activity_step.setVisibility(View.VISIBLE);
          ProgressBarDrawable progStep = new ProgressBarDrawable(size);
          pb_activity_step.setProgressDrawable(progStep);
  
          int count = 0;
          for (int i = 0; i < size; i++) {
            if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.PENDING)) {
              tv_activity_step_status.setText(getActivityStatusString(ActivityStatus.PENDING));
              tv_activity_step_status_message.setText(commItem.getTaskItem().getActivities().get(i).getName());
              break;
            }
            if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.RUNNING)) {
              tv_activity_step_status.setText(getActivityStatusString(ActivityStatus.RUNNING));
              tv_activity_step_status_message.setText(commItem.getTaskItem().getActivities().get(i).getName());
              count++;
              break;
            }
            if (commItem.getTaskItem().getActivities().get(i).getStatus().equals(ActivityStatus.FINISHED)) {
              tv_activity_step_status.setText(getActivityStatusString(ActivityStatus.FINISHED));
              tv_activity_step_status_message.setText(commItem.getTaskItem().getActivities().get(i).getName());
              count += 2;
              continue;
            }
          }
          if (count == 0) {
            pb_activity_step.setProgress(0);
          } else if (count == size * 2) {
            pb_activity_step.setProgress(100);
          } else {
            pb_activity_step.setProgress(Math.round((100.0f / (size * 2.0f)) * count));
          }
        } else {
          pb_activity_step.setProgress(0);
          pb_activity_step.setVisibility(View.GONE);
        }
      }
      
      // Mandant Name and ID:
      if (commItem.getTaskItem().getMandantId() != null || commItem.getTaskItem().getMandantName() != null) {
        tv_client.setVisibility(View.VISIBLE);
        tv_client_title.setVisibility(View.VISIBLE);
        
        String client = "";
        if (commItem.getTaskItem().getMandantName() != null) {
          client += commItem.getTaskItem().getMandantName() + " ";
        }
        if (commItem.getTaskItem().getMandantId() != null) {
          client += "(" + commItem.getTaskItem().getMandantId() + ")";
        }
        tv_client.setText(client);
      } else {
        tv_client.setVisibility(View.GONE);
        tv_client_title.setVisibility(View.GONE);
      }
      
      // Load Data:
      if (commItem.getTaskItem().getTaskDetails() != null || commItem.getTaskItem().getDescription() != null) {
        // Waren:
        if (commItem.getTaskItem().getTaskDetails().getDescription() != null) {
          applyInfoReference(commItem.getTaskItem().getTaskDetails().getDescription());
        } else if (commItem.getTaskItem().getDescription() != null) {
          applyInfoReference(commItem.getTaskItem().getDescription());
        } else {
          applyInfoReference(null);
        }
        
        // Loading Order:
        if (commItem.getTaskItem().getTaskDetails().getLoadingOrder() != null) {
          applyLoadingOrder(commItem.getTaskItem().getTaskDetails().getLoadingOrder());
        } else {
          applyLoadingOrder(null);
        }
      } else {
        applyInfoReference(null);
        applyLoadingOrder(null);
      }
      applyRefId1(commItem.getTaskItem().getReferenceIdCustomer1());
      applyRefId2(commItem.getTaskItem().getReferenceIdCustomer2());
      applyDangerousGoods(commItem.getTaskItem().getDangerousGoods());
      applyPallets(commItem.getTaskItem().getPalletExchange());
      applyNotes(commItem.getTaskItem().getNotes());
      applyContacts(commItem.getTaskItem().getContacts());
      applyDocuments(commItem.getDocumentItem());
  
      List<String> photos = item.getPhotoUrls();
      int photoBadge = 0;
      int uploadedPhoto = 0;
      if (photos.size() > 0) {
        for (int i = 0; i < photos.size(); i++) {
          UploadItem uploadItem = App.getGson().fromJson(photos.get(i), UploadItem.class);
          if (uploadItem != null) {
            if (uploadItem.getUploaded()) {
              uploadedPhoto++;
              continue;
            }
        
            photoBadge++;
          }
        }
      }
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
      }
    }
  }
  
  public void setOnItemListener(OnItemClickListener listener) {
    this.listener = listener;
  }
  
  private void applyDocuments(DocumentItem document) {
    if (document == null) {
      btn_document_info.setVisibility(View.GONE);
      return;
    }
    
    btn_document_info.setVisibility(View.VISIBLE);
  }
  
  private void applyDangerousGoods(DangerousGoods dangerousGoods) {
    if (dangerousGoods == null) {
      btn_dangerous_goods_info.setVisibility(View.GONE);
      btn_dangerous_goods_info.setImageDrawable(null);
      
      iv_dangerous_goods.setVisibility(View.GONE);
      iv_dangerous_goods.setImageDrawable(null);
      tv_adr_class_title.setVisibility(View.GONE);
      tv_adr_class.setVisibility(View.GONE);
      tv_un_no_title.setVisibility(View.GONE);
      tv_un_no.setVisibility(View.GONE);
      
      ll_dangerous_goods.setVisibility(View.GONE);
      return;
    }
    if (dangerousGoods.isGoodsDangerous() != null) {
      if (dangerousGoods.isGoodsDangerous()) {
        ll_dangerous_goods.setVisibility(View.VISIBLE);
        
        btn_dangerous_goods_info.setVisibility(View.VISIBLE);
        btn_dangerous_goods_info.setImageDrawable(null);
        
        iv_dangerous_goods.setVisibility(View.VISIBLE);
        iv_dangerous_goods.setImageDrawable(null);
  
        if (dangerousGoods.getDangerousGoodsClassType() != null) {
          btn_dangerous_goods_info.setImageDrawable(getDangerousGoodsClass(dangerousGoods.getDangerousGoodsClassType()));
          iv_dangerous_goods.setImageDrawable(getDangerousGoodsClass(dangerousGoods.getDangerousGoodsClassType()));
        }
        tv_adr_class_title.setVisibility(View.VISIBLE);
        tv_adr_class.setVisibility(View.VISIBLE);
        if (dangerousGoods.getAdrClass() != null) {
          tv_adr_class.setText(dangerousGoods.getAdrClass());
        }
        tv_un_no_title.setVisibility(View.VISIBLE);
        tv_un_no.setVisibility(View.VISIBLE);
        if (dangerousGoods.getUnNo() != null) {
          tv_un_no.setText(dangerousGoods.getUnNo());
        }
      } else {
        btn_dangerous_goods_info.setVisibility(View.GONE);
        btn_dangerous_goods_info.setImageDrawable(null);
  
        iv_dangerous_goods.setVisibility(View.GONE);
        iv_dangerous_goods.setImageDrawable(null);
        tv_adr_class_title.setVisibility(View.GONE);
        tv_adr_class.setVisibility(View.GONE);
        tv_un_no_title.setVisibility(View.GONE);
        tv_un_no.setVisibility(View.GONE);
        
        ll_dangerous_goods.setVisibility(View.GONE);
      }
    }
  }
  
  private void applyPallets(PalletExchange palletExchange) {
    if (palletExchange == null) {
      btn_palette_info.setVisibility(View.GONE);
      
      iv_palette_info.setVisibility(View.GONE);
      tv_number_of_paletts_title.setVisibility(View.GONE);
      tv_number_of_paletts.setVisibility(View.GONE);
      tv_dpl_title.setVisibility(View.GONE);
      tv_dpl.setVisibility(View.GONE);
      
      ll_paletts.setVisibility(View.GONE);
      return;
    }
    if(palletExchange.getPalletExchangeType().equals(EnumPalletExchangeType.YES)) {
      ll_paletts.setVisibility(View.VISIBLE);
      btn_palette_info.setVisibility(View.VISIBLE);
      
      tv_number_of_paletts_title.setVisibility(View.VISIBLE);
      if (palletExchange.getPalletsAmount() != null) {
        tv_number_of_paletts.setVisibility(View.VISIBLE);
        tv_number_of_paletts.setText(String.valueOf(palletExchange.getPalletsAmount().intValue()));
      } else {
        tv_number_of_paletts.setVisibility(View.GONE);
      }
      
      tv_dpl_title.setVisibility(View.VISIBLE);
      if (palletExchange.isDPL() != null) {
        tv_dpl.setVisibility(View.VISIBLE);
        tv_dpl.setText(palletExchange.isDPL() ? _resources.getString(R.string.yes) : _resources.getString(R.string.no));
      } else {
        tv_dpl.setVisibility(View.GONE);
      }
    } else {
      btn_palette_info.setVisibility(View.GONE);
      ll_paletts.setVisibility(View.GONE);
    }
  }
  
  private void applyNotes(List<NotesItem> notes) {
    if (notes == null || notes.size() <= 0) {
      btn_notes_info.setVisibility(View.GONE);
      iv_notes.setVisibility(View.GONE);
      tv_notes_title.setVisibility(View.GONE);
      tv_notes.setVisibility(View.GONE);
      tv_header_notes_minus.setVisibility(View.GONE);
      tv_header_notes.setVisibility(View.GONE);
      ll_notes.setVisibility(View.GONE);
      return;
    }
    
    ll_notes.setVisibility(View.VISIBLE);
    
    iv_notes.setVisibility(View.VISIBLE);
    btn_notes_info.setVisibility(View.VISIBLE);
    tv_notes_title.setVisibility(View.VISIBLE);
    tv_notes.setVisibility(View.VISIBLE);
    
    String comment_text = _resources.getQuantityString(R.plurals.numberOfNotesAvailable, notes.size(), notes.size());
    tv_notes.setText(comment_text);
    
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
      iv_contacts.setVisibility(View.GONE);
      tv_contact_title.setVisibility(View.GONE);
      
      ll_contacts.setVisibility(View.GONE);
      return;
    }
    
    ll_contacts.setVisibility(View.VISIBLE);
    btn_contact_info.setVisibility(View.VISIBLE);
    iv_contacts.setVisibility(View.VISIBLE);
    tv_contact_title.setVisibility(View.VISIBLE);
    tv_contact.setVisibility(View.VISIBLE);
    String contact_text = String.valueOf(contacts.size()) + " " + _resources.getString(R.string.contacts_available);
    tv_contact.setText(contact_text);
  }
  
  private void applyRefId1(String refId1) {
    if (refId1 == null) {
      tv_ref_id_1.setVisibility(View.GONE);
      tv_ref_id_1_title.setVisibility(View.GONE);
      return;
    }
    
    tv_ref_id_1.setVisibility(View.VISIBLE);
    tv_ref_id_1_title.setVisibility(View.VISIBLE);
    tv_ref_id_1.setText(refId1);
  }
  
  private void applyRefId2(String refId2) {
    if (refId2 == null) {
      tv_ref_id_2.setVisibility(View.GONE);
      tv_ref_id_2_title.setVisibility(View.GONE);
      return;
    }
    
    tv_ref_id_2.setVisibility(View.VISIBLE);
    tv_ref_id_2_title.setVisibility(View.VISIBLE);
    tv_ref_id_2.setText(refId2);
  }
  
  private void applyInfoReference(String infoReference) {
    if (infoReference == null) {
      tv_info_reference_title.setVisibility(View.GONE);
      tv_info_reference.setVisibility(View.GONE);
      return;
    }
    
    tv_info_reference_title.setVisibility(View.VISIBLE);
    tv_info_reference.setVisibility(View.VISIBLE);
    tv_info_reference.setText(infoReference);
  }
  
  private void applyLoadingOrder(Integer loadingOrder) {
    if (loadingOrder == null) {
      tv_loading_order_title.setVisibility(View.GONE);
      tv_loading_order.setVisibility(View.GONE);
      return;
    }
    
    tv_loading_order_title.setVisibility(View.VISIBLE);
    tv_loading_order.setVisibility(View.VISIBLE);
    tv_loading_order.setText(String.valueOf(loadingOrder.intValue()));
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
      default: return null;
    }
  }
}
